package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.MailModel;
import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import com.example.Capstone_sito_web_personal_trainer.entities.Servizio;
import com.example.Capstone_sito_web_personal_trainer.exception.ClosedException;
import com.example.Capstone_sito_web_personal_trainer.payload.PrenotazioneDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.PrenotazioneMapperDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.request.CreaPrenotazioneRequest;
import com.example.Capstone_sito_web_personal_trainer.repositories.PrenotazioneRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.ServizioRepository;
import com.example.Capstone_sito_web_personal_trainer.security.services.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private ServizioRepository servizioRepository;

    @Autowired
    private PrenotazioneMapperDTO prenotazioneMapperDTO;

    @Autowired
    MailService mailService;


    public PrenotazioneDTO creaPrenotazione(CreaPrenotazioneRequest prenotazioneDTO) {
        // Recupero il servizio
        Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                .orElseThrow(() -> new EntityNotFoundException("⚠️ Servizio non trovato! ⚠️"));

        // Ricavo l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Utente utenteLoggato = userDetails.getUser();


        LocalDateTime dataOraPrenotazione = prenotazioneDTO.getDataOraPrenotazione();
        int durata = servizio.getDurata(); // Durata in minuti
        LocalDateTime finePrenotazione = dataOraPrenotazione.plusMinutes(durata);

        // Controllo per verificare che la data inserita non sia passata
        if (dataOraPrenotazione.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("⚠️ Non è possibile prenotare un appuntamento per una data passata ⚠️");
        }

        //controllo per vedere se la prenotazione è di domenica
        if (prenotazioneDTO.getDataOraPrenotazione().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new ClosedException("⚠️ Non si possono prenotare appuntamenti la domenica ⚠️");
        }

        // Se la prenotazione è per sabato, l'ultimo appuntamento deve finire entro le 13:00
        if (dataOraPrenotazione.getDayOfWeek() == DayOfWeek.SATURDAY) {
            LocalTime orarioFine = dataOraPrenotazione.toLocalTime().plusMinutes(durata);

            if (orarioFine.isAfter(LocalTime.of(13, 0))) {
                throw new ClosedException("⚠️ Il sabato gli appuntamenti devono concludersi entro le 13:00 ⚠️");
            }
        }

        //ORARIO CHIUSURA STUDIO ORE 20:30, se il servizio finisce dopo non si può prenotare
        if (finePrenotazione.toLocalTime().isAfter(LocalTime.of(20, 30))) {
            throw new ClosedException("⚠️ Gli appuntamenti devono concludersi entro le 20:30 ⚠️");
        }

        // Controllo per vedere se ci sono sovrapposizioni con altre prenotazioni
        if (isSovrapposizione(dataOraPrenotazione, finePrenotazione)) {
            throw new IllegalArgumentException("⚠️ Impossibile creare una prenotazione per l'ora e la data selezionata ⚠️");
        }


        // Creo la nuova prenotazione
        Prenotazione prenotazione = prenotazioneMapperDTO.requestToEntity(prenotazioneDTO);
        prenotazione.setUtente(utenteLoggato);
        prenotazione.setServizio(servizio);
        prenotazione.setDataOra(dataOraPrenotazione);

        // Salvo la prenotazione
        prenotazione = prenotazioneRepository.save(prenotazione);

        String destinatario = utenteLoggato.getEmail();
        String oggetto = "Conferma Prenotazione - " + servizio.getNomeServizio();

        String contenuto = "Ciao " + utenteLoggato.getNome() + ",\n\n"
                + "La tua prenotazione per il servizio '" + servizio.getNomeServizio() + "' è stata confermata.\n"
                + "Dettagli:\n"
                + "📅 Data: " + dataOraPrenotazione.toLocalDate() + "\n"
                + "🕒 Orario: " + dataOraPrenotazione.toLocalTime() + "\n"
                + "⌛ Durata: " + durata + " minuti\n\n"
                + "Grazie!\n"
                + "Cordiali saluti,\nAlessandro";

        //Creo l'oggetto per la mail
        MailModel mailModel = new MailModel();

        mailModel.setDestinatario(destinatario);
        mailModel.setOggetto(oggetto);
        mailModel.setContenuto(contenuto);

        mailService.inviaEmail(mailModel);

        return prenotazioneMapperDTO.toDto(prenotazione);
    }

    public PrenotazioneDTO getPrenotazioneById(Long id) {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata"));
        return prenotazioneMapperDTO.toDto(prenotazione);
    }

    public List<PrenotazioneDTO> getAllPrenotazioni() {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findAll();
        return prenotazioni.stream().map(prenotazioneMapperDTO::toDto).collect(Collectors.toList());
    }

    public void cancellaPrenotazione(Long id) throws AccessDeniedException {
        if (!prenotazioneRepository.existsById(id)) {
            throw new EntityNotFoundException("⚠️ Prenotazione non trovata ️ ️⚠️");
        }

        // Ricavo l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Utente utenteLoggato = userDetails.getUser();

        // Trovo la prenotazione
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("⚠️ Prenotazione non trovata ️ ️⚠️"));

        // Controllo per vedere se la prenotazione appartiene all'utente loggato
        if (!prenotazione.getUtente().getId().equals(utenteLoggato.getId())) {
            throw new AccessDeniedException("⚠️ Non puoi cancellare questa prenotazione ⚠️");
        }

        prenotazioneRepository.deleteById(id);
    }

    public PrenotazioneDTO modificaPrenotazione(Long id, PrenotazioneDTO prenotazioneDTO) throws AccessDeniedException {
        //Recupero la prenotazione nel db
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(" ⚠️ Prenotazione non trovata ⚠️"));

        // recupero l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Utente utenteLoggato = userDetails.getUser();

        // Controllo per vedere se la prenotazione appartiene all'utente loggato
        if (!prenotazione.getUtente().getId().equals(utenteLoggato.getId())) {
            throw new AccessDeniedException("⚠️ Non puoi modificare questa prenotazione ⚠️");
        }

        // PER MODIFICARE LA DATA DI UNA PRENOTAZIONE BISOGNA INSERIRE ANCHE L'ID DEL SERVIZIO
        if (prenotazioneDTO.getDataOraPrenotazione() != null) {
            // Recupera il servizio scelto e la durata
            Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                    .orElseThrow(() -> new EntityNotFoundException("⚠️ Servizio non trovato!⚠️"));
            prenotazione.setServizio(servizio);

            LocalDateTime dataOraPrenotazione = prenotazioneDTO.getDataOraPrenotazione();
            int durata = servizio.getDurata(); // Durata in minuti
            LocalDateTime finePrenotazione = dataOraPrenotazione.plusMinutes(durata);

            // Controllo per verificare che la data inserita non sia passaat
            if (dataOraPrenotazione.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("⚠️ Non è possibile prenotare un appuntamento per una data passata ⚠️");
            }

            //controllo per vedere se la prenotazione è di domenica
            if (prenotazioneDTO.getDataOraPrenotazione().getDayOfWeek() == DayOfWeek.SUNDAY) {
                throw new ClosedException("⚠️ Non si possono prenotare appuntamenti la domenica ⚠️");
            }

            // Se la prenotazione è per sabato, l'ultimo appuntamento deve finire entro le 13:00
            if (dataOraPrenotazione.getDayOfWeek() == DayOfWeek.SATURDAY) {
                LocalTime orarioFine = dataOraPrenotazione.toLocalTime().plusMinutes(durata);

                if (orarioFine.isAfter(LocalTime.of(13, 0))) {
                    throw new ClosedException("⚠️ Il sabato gli appuntamenti devono concludersi entro le 13:00 ⚠️");
                }
            }

            //ORARIO CHIUSURA STUDIO ORE 20:30, se il servizio finisce dopo non si può prenotare
            if (finePrenotazione.toLocalTime().isAfter(LocalTime.of(20, 30))) {
                throw new ClosedException("⚠️ Gli appuntamenti devono concludersi entro le 20:30 ⚠️");
            }

            // Controllo per vedere se ci sono sovrapposizioni con altre prenotazioni
            if (isSovrapposizione(dataOraPrenotazione, finePrenotazione)) {
                throw new IllegalArgumentException("⚠️ Impossibile creare una prenotazione per l'ora e la data selezionata ⚠️");
            }

            prenotazione.setDataOra(dataOraPrenotazione); // Aggiorna la data e ora della prenotazione
        }

        // Se viene fornito un nuovo servizio, aggiorna
        if (prenotazioneDTO.getServizioId() != null) {
            Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                    .orElseThrow(() -> new EntityNotFoundException("⚠️  ️Servizio non trovato! ⚠️"));
            prenotazione.setServizio(servizio);
        }

        // Se viene fornito un nuovo utente, aggiorna
        if (prenotazioneDTO.getUtenteId() != null) {
            Utente utente = utenteRepository.findById(prenotazioneDTO.getUtenteId())
                    .orElseThrow(() -> new EntityNotFoundException("⚠️ Utente non trovato! ⚠️"));
            prenotazione.setUtente(utente);
        }

        // Se vengono inserite nuove note, aggiorna
        if (prenotazioneDTO.getNote() != null) {
            prenotazione.setNote(prenotazioneDTO.getNote());
        }

        // Salvo la prenotazione aggiornata
        prenotazione = prenotazioneRepository.save(prenotazione);
        return prenotazioneMapperDTO.toDto(prenotazione);
    }

    // Tutte le prenotazioni di un cliente
    public List<PrenotazioneDTO> getPrenotazioniByUtente(Long utenteId) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new EntityNotFoundException("⚠️ Utente non trovato! ⚠️"));

        List<Prenotazione> prenotazioni = prenotazioneRepository.findByUtente(utente);
        return prenotazioni.stream()
                .map(prenotazioneMapperDTO::toDto)
                .collect(Collectors.toList());
    }


    // Metodo per verificare la sovrapposizione delle date e ore
    private boolean isSovrapposizione(LocalDateTime start, LocalDateTime end) {
        List<Prenotazione> prenotazioniEsistenti = prenotazioneRepository.findAll();

        // Controllo per vedere se la nuova prenotazione si sovrappone a quelle esistenti
        for (Prenotazione prenotazione : prenotazioniEsistenti) {
            LocalDateTime inizioEsistente = prenotazione.getDataOra();
            int durataServizio = prenotazione.getServizio().getDurata();
            LocalDateTime fineEsistente = inizioEsistente.plusMinutes(durataServizio);

            // Controllo per vedere se l'intervallo di tempo della nuova prenotazione si sovrappone con uno esistente
            if (start.isBefore(fineEsistente) && end.isAfter(inizioEsistente)) {
                return true; // sovrapposizione
            }
        }
        return false; // Nessuna sovrapposizione
    }


}
