package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.*;
import com.example.Capstone_sito_web_personal_trainer.exception.ClosedException;
import com.example.Capstone_sito_web_personal_trainer.payload.PrenotazioneDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.PrenotazioneMapperDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.request.CreaPrenotazioneRequest;
import com.example.Capstone_sito_web_personal_trainer.repositories.IndirizzoRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.PrenotazioneRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.ServizioRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import com.example.Capstone_sito_web_personal_trainer.security.services.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    @Autowired
    MailService mailService;
    @Autowired
    IndirizzoRepository indirizzoRepository;
    @Autowired
    private PrenotazioneRepository prenotazioneRepository;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private ServizioRepository servizioRepository;
    @Autowired
    private PrenotazioneMapperDTO prenotazioneMapperDTO;

    public PrenotazioneDTO creaPrenotazione(CreaPrenotazioneRequest prenotazioneDTO) {
        // Recupero il servizio
        Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                .orElseThrow(() -> new EntityNotFoundException(" Servizio non trovato! "));

        // Ricavo l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Utente utenteLoggato = userDetails.getUser();

        // Per creare una prenotazione per un utente normale quando mi loggo come ADMIN o PERSONAL_TRAINER
        //controllo ruolo e tramite l'id dell'utente selezionato, recupera i dati di quell'utente e non dell'admin
        Utente utentePrenotazione;
        if ((utenteLoggato.getRuolo().name().equals("ADMIN") || utenteLoggato.getRuolo().name().equals("PERSONAL_TRAINER"))
                && prenotazioneDTO.getUtenteId() != null) {
            utentePrenotazione = utenteRepository.findById(prenotazioneDTO.getUtenteId())
                    .orElseThrow(() -> new EntityNotFoundException("Utente per la prenotazione non trovato!"));
        } else {
            utentePrenotazione = utenteLoggato;
        }

        LocalDateTime dataOraPrenotazione = prenotazioneDTO.getDataOraPrenotazione();
        int durata = servizio.getDurata(); // Durata in minuti
        LocalDateTime finePrenotazione = dataOraPrenotazione.plusMinutes(durata);
        LocalTime oraInizio = dataOraPrenotazione.toLocalTime();

        // Controllo per verificare che la data inserita non sia passata
        if (dataOraPrenotazione.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(" Non è possibile prenotare un appuntamento per una data passata ");
        }

        //controllo per vedere se la prenotazione è di domenica
        if (prenotazioneDTO.getDataOraPrenotazione().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new ClosedException(" Non si possono prenotare appuntamenti la domenica ");
        }

        // Se la prenotazione è per sabato, l'ultimo appuntamento deve finire entro le 13:00
        if (dataOraPrenotazione.getDayOfWeek() == DayOfWeek.SATURDAY) {
            LocalTime orarioFine = dataOraPrenotazione.toLocalTime().plusMinutes(durata);

            if (orarioFine.isAfter(LocalTime.of(13, 0))) {
                throw new ClosedException("Il sabato gli appuntamenti devono concludersi entro le 13:00 ");
            }
        }

        //ORARIO APERTURA STUDIO ORE 9:00, non si possono prendere appunntamenti ceh iniziano prima
        if (oraInizio.isBefore(LocalTime.of(9, 0))) {
            throw new IllegalArgumentException(" Non è possibile prenotare appuntamenti prima delle 9:00");
        }

        //ORARIO CHIUSURA STUDIO ORE 20:30, se il servizio finisce dopo non si può prenotare
        if (finePrenotazione.toLocalTime().isAfter(LocalTime.of(20, 30))) {
            throw new ClosedException("Gli appuntamenti devono concludersi entro le 20:30 ");
        }

        // Controllo per vedere se ci sono sovrapposizioni con altre prenotazioni
        if (isSovrapposizione(dataOraPrenotazione, finePrenotazione, null)) {
            throw new IllegalArgumentException("Impossibile creare una prenotazione per l'ora e la data selezionata, c'è gia un altro appuntamento prenotato");
        }


        // Creo la nuova prenotazione
        Prenotazione prenotazione = prenotazioneMapperDTO.requestToEntity(prenotazioneDTO);
        prenotazione.setUtente(utentePrenotazione);
        prenotazione.setServizio(servizio);
        prenotazione.setDataOra(dataOraPrenotazione);

        // Salvo la prenotazione
        prenotazione = prenotazioneRepository.save(prenotazione);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String dataFormattata = dataOraPrenotazione.format(formatter);
        String destinatario = utentePrenotazione.getEmail();
        String oggetto = "Conferma Prenotazione - " + servizio.getNomeServizio();

        String contenuto = "Ciao " + utentePrenotazione.getNome() + ",\n\n"
                + "La tua prenotazione per il servizio '" + servizio.getNomeServizio() + "' è stata confermata.\n\n"
                + "Dettagli:\n\n"
                + "📅 Data: " + dataFormattata + "\n"
                + "🕒 Orario: " + dataOraPrenotazione.toLocalTime() + "\n"
                + "⌛ Durata: " + durata + " minuti\n"
                + "📍 Indirizzo: " + prenotazione.getIndirizzo().getCitta() + ",  " + prenotazione.getIndirizzo().getVia() + " " + prenotazione.getIndirizzo().getNumeroCivico() + " - " + prenotazione.getIndirizzo().getNomeStudio() + "\n"
                + (!Objects.equals(prenotazione.getNote(), "") ? "📝 Note: " + prenotazione.getNote() + "\n\n" : "\n\n") // se non vengono aggiunte note non viene mostrato niente
                + "Grazie!\n"
                + "A presto, cordiali saluti,\n\nDott.Alessandro";

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
                .orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata ️ ️"));
        return prenotazioneMapperDTO.toDto(prenotazione);
    }

    //mostra tutte le prenotazioni future, quelle con data passata rispetto alla data di oggi non verranno mostrate nel front-end
    public List<PrenotazioneDTO> getAllPrenotazioni() {
        LocalDateTime now = LocalDateTime.now();
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByDataOraAfter(now);
        return prenotazioni.stream().map(prenotazioneMapperDTO::toDto).collect(Collectors.toList());
    }

    public void cancellaPrenotazione(Long id) throws AccessDeniedException {
        if (!prenotazioneRepository.existsById(id)) {
            throw new EntityNotFoundException("Prenotazione non trovata ️ ️");
        }

        // Ricavo l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Utente utenteLoggato = userDetails.getUser();


        // Trovo la prenotazione
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata ️ ️"));

        // Controllo per vedere se la prenotazione appartiene all'utente loggato
        if (prenotazione.getUtente().getId().equals(utenteLoggato.getId()) || utenteLoggato.getRuolo().name().equals("ADMIN") || utenteLoggato.getRuolo().name().equals("PERSONAL_TRAINER")) {
            prenotazioneRepository.deleteById(id);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = prenotazione.getDataOra().format(formatter);

            // Preparazione e invio della mail
            String destinatario = prenotazione.getUtente().getEmail();
            String oggetto = "Cancellazione Prenotazione - " + prenotazione.getServizio().getNomeServizio();

            String contenuto = "Ciao " + prenotazione.getUtente().getNome() + ",\n\n"
                    + "La tua prenotazione per il servizio '" + prenotazione.getServizio().getNomeServizio() + " prevista per il " + dataFormattata + " alle: " + prenotazione.getDataOra().toLocalTime() + " a " + prenotazione.getIndirizzo().getCitta() + ", " + prenotazione.getIndirizzo().getVia() + " " + prenotazione.getIndirizzo().getNumeroCivico() + " - " + prenotazione.getIndirizzo().getNomeStudio() + " è stata cancellata con successo.\n\n"
                    + "Se la cancellazione è avvenuta per errore o desideri prenotare un nuovo appuntamento,\n"
                    + "puoi farlo direttamente accedendo alla tua area personale o contattandomi.\n\n"
                    + "Grazie!\n"
                    + "A presto, cordiali saluti,\n\nDott.Alessandro";

            MailModel mailModel = new MailModel();
            mailModel.setDestinatario(destinatario);
            mailModel.setOggetto(oggetto);
            mailModel.setContenuto(contenuto);

            mailService.inviaEmail(mailModel);

        } else {
            throw new AccessDeniedException("Non sei autorizzato a cancellare questa prenotazione");
        }

    }

    public PrenotazioneDTO modificaPrenotazione(Long id, PrenotazioneDTO prenotazioneDTO) throws AccessDeniedException {
        //recupero la prenotazione nel db
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata "));

        // recupero l'utente loggato dal contesto di sicurezza
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Utente utenteLoggato = userDetails.getUser();

        // controllo per vedere se la prenotazione appartiene all'utente loggato
        if (prenotazione.getUtente().getId().equals(utenteLoggato.getId()) || utenteLoggato.getRuolo().name().equals("ADMIN") || utenteLoggato.getRuolo().name().equals("PERSONAL_TRAINER")) {

            // ---------- PER MODIFICARE LA DATA DI UNA PRENOTAZIONE BISOGNA INSERIRE ANCHE L'ID DEL SERVIZIO ----------
            if (prenotazioneDTO.getDataOraPrenotazione() != null) {
                // Recupera il servizio scelto e la durata
                Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                        .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato!"));
                prenotazione.setServizio(servizio);

                LocalDateTime dataOraPrenotazione = prenotazioneDTO.getDataOraPrenotazione();
                int durata = servizio.getDurata(); // Durata in minuti
                LocalDateTime finePrenotazione = dataOraPrenotazione.plusMinutes(durata);
                LocalTime oraInizio = dataOraPrenotazione.toLocalTime();

                // Controllo per verificare che la data inserita non sia passaat
                if (dataOraPrenotazione.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Non è possibile prenotare un appuntamento per una data passata ");
                }

                //controllo per vedere se la prenotazione è di domenica
                if (prenotazioneDTO.getDataOraPrenotazione().getDayOfWeek() == DayOfWeek.SUNDAY) {
                    throw new ClosedException("Non si possono prenotare appuntamenti la domenica ");
                }

                // Se la prenotazione è per sabato, l'ultimo appuntamento deve finire entro le 13:00
                if (dataOraPrenotazione.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    LocalTime orarioFine = dataOraPrenotazione.toLocalTime().plusMinutes(durata);

                    if (orarioFine.isAfter(LocalTime.of(13, 0))) {
                        throw new ClosedException("Il sabato gli appuntamenti devono concludersi entro le 13:00 ");
                    }
                }

                //ORARIO APERTURA STUDIO ORE 9:00, non si possono prendere appunntamenti ceh iniziano prima
                if (oraInizio.isBefore(LocalTime.of(9, 0))) {
                    throw new IllegalArgumentException(" Non è possibile prenotare appuntamenti prima delle 9:00");
                }

                //ORARIO CHIUSURA STUDIO ORE 20:30, se il servizio finisce dopo non si può prenotare
                if (finePrenotazione.toLocalTime().isAfter(LocalTime.of(20, 30))) {
                    throw new ClosedException("Gli appuntamenti devono concludersi entro le 20:30 ");
                }

                // Controllo per vedere se ci sono sovrapposizioni con altre prenotazioni
                if (isSovrapposizione(dataOraPrenotazione, finePrenotazione, prenotazione.getId())) {
                    throw new IllegalArgumentException("Impossibile creare una prenotazione per l'ora e la data selezionata, c'è gia un altro appuntamento prenotato ");
                }

                prenotazione.setDataOra(dataOraPrenotazione); // Aggiorna la data e ora della prenotazione
            }

            // Se viene fornito un nuovo servizio, aggiorna
            if (prenotazioneDTO.getServizioId() != null) {
                Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                        .orElseThrow(() -> new EntityNotFoundException(" ️Servizio non trovato! "));
                prenotazione.setServizio(servizio);
            }

            // Se viene fornito un nuovo utente, aggiorna
            if (prenotazioneDTO.getUtenteId() != null) {
                Utente utente = utenteRepository.findById(prenotazioneDTO.getUtenteId())
                        .orElseThrow(() -> new EntityNotFoundException("Utente non trovato! "));
                prenotazione.setUtente(utente);
            }

            // Se vengono inserite nuove note, aggiorna
            if (prenotazioneDTO.getNote() != null) {
                prenotazione.setNote(prenotazioneDTO.getNote());
            }

            //se viene forinto un nuovo indirizzo, aggiorna
            if (prenotazioneDTO.getIndirizzoId() != null) {
                Indirizzo indirizzo = indirizzoRepository.findById(prenotazioneDTO.getIndirizzoId()).orElseThrow(() -> new EntityNotFoundException("Indirizzo non trovato"));
                prenotazione.setIndirizzo(indirizzo);
            }

            // Salvo la prenotazione aggiornata
            prenotazione = prenotazioneRepository.save(prenotazione);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormattata = prenotazione.getDataOra().format(formatter);

            // Preparazione e invio della mail
            String destinatario = prenotazione.getUtente().getEmail();
            String oggetto = "Aggiornamento Prenotazione - " + prenotazione.getServizio().getNomeServizio();

            String contenuto = "Ciao " + prenotazione.getUtente().getNome() + ",\n\n"
                    + "La tua prenotazione per il servizio '" + prenotazione.getServizio().getNomeServizio() + "' è stata aggiornata.\n\n"
                    + "Dettagli aggiornati:\n\n"
                    + "📅 Data: " + dataFormattata + "\n"
                    + "🕒 Orario: " + prenotazione.getDataOra().toLocalTime() + "\n"
                    + "⌛ Durata: " + prenotazione.getServizio().getDurata() + " minuti\n"
                    + "📍 Indirizzo: " + prenotazione.getIndirizzo().getCitta() + ",  " + prenotazione.getIndirizzo().getVia() + " " + prenotazione.getIndirizzo().getNumeroCivico() + " - " + prenotazione.getIndirizzo().getNomeStudio() + "\n"
                    + (prenotazione.getNote() != null ? "📝 Note: " + prenotazione.getNote() + "\n\n" : "")
                    + "Grazie!\n"
                    + "A presto, cordiali saluti,\n\nDott.Alessandro";

            MailModel mailModel = new MailModel();
            mailModel.setDestinatario(destinatario);
            mailModel.setOggetto(oggetto);
            mailModel.setContenuto(contenuto);

            mailService.inviaEmail(mailModel);

            return prenotazioneMapperDTO.toDto(prenotazione);

        } else {
            throw new AccessDeniedException("Non puoi modificare questa prenotazione ");
        }


    }

    //tutte le prenotazioni di un cliente
    public List<PrenotazioneDTO> getPrenotazioniByUtente(Long utenteId) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato! "));

        LocalDateTime now = LocalDateTime.now();
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByDataOraAfterAndUtente(now, utente);
        return prenotazioni.stream()
                .map(prenotazioneMapperDTO::toDto)
                .collect(Collectors.toList());
    }

    //metodo per trovare tutti gli orari disponibili in un giorno selezionato
    public List<LocalDateTime> getOrariDisponibili(LocalDate data, Long servizioId) {
        Servizio servizio = servizioRepository.findById(servizioId)
                .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato!"));

        LocalTime inizioOrario = LocalTime.of(9, 0);  // Apertura
        LocalTime fineOrario = LocalTime.of(20, 30);  // Chiusura


        LocalTime fineOrarioSabato = LocalTime.of(13, 0);//chiusura sabato
        int durataServizio = servizio.getDurata();  // Durata del servizio scelto

        List<LocalDateTime> orariDisponibili = new ArrayList<>();

        //ricavo tutte le prenotazioni per il giorno selezionato
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByDataOraBetween(
                data.atStartOfDay(),
                data.atTime(23, 59) // Fino alla fine della giornata
        );

        // Tutti gli orari occupati
        Set<LocalTime> orariOccupati = new HashSet<>();
        for (Prenotazione p : prenotazioni) {
            LocalTime inizioOccupato = p.getDataOra().toLocalTime(); //ricavo orario inizio prenotazione
            int durata = p.getServizio().getDurata(); //ricavo durata preotazione
            LocalTime fineOccupato = inizioOccupato.plusMinutes(durata);//ricavo orario fine prenotazione

            while (inizioOccupato.isBefore(fineOccupato)) {
                //tutti gli intervalli, di 15min in 15min tra l'inizio e la fine della prenotazione verranno aggiunti al set di orariOccupati
                //non verranno mostrati tra i disponibili e in quelgli orari n on sarà possibile fare una prenotazione
                //ES inizio 10:00 fine 10:30 ---> 10:00 - 10:15 - 10:30 non saranno disponibili il prossimo disponiibile sarà 10:45
                orariOccupati.add(inizioOccupato);
                inizioOccupato = inizioOccupato.plusMinutes(15);

            }
        }

        // Tutti gli orari disponibili
        LocalTime orarioCorrente = inizioOrario;
        // Se è sabato, usa il fine orario sabato, altrimenti il normale
        LocalTime orarioFineGiornata = data.getDayOfWeek().equals(DayOfWeek.SATURDAY) ? fineOrarioSabato : fineOrario;

        while (orarioCorrente.plusMinutes(durataServizio).isBefore(orarioFineGiornata) ||
                orarioCorrente.plusMinutes(durataServizio).equals(orarioFineGiornata)) {

            boolean occupato = false;

            // Controllo per vedere se tutti gli slot necessari per la durata del servizio sono disponibili
            //sempre con un intervallo di 15 min se quell'orario è presente nella lista degli orariOccupati non lo aggiunge
            for (int i = 0; i < durataServizio / 15; i++) {
                if (orariOccupati.contains(orarioCorrente.plusMinutes(i * 15))) {
                    occupato = true;
                    break;
                }
            }
            // altrimenti lo aggiunge alla lista degli orari disponibili
            if (!occupato) {
                orariDisponibili.add(LocalDateTime.of(data, orarioCorrente));
            }
            //aggiungo semrpre 15 min per avere gli orari disponibili di 15 in 15
            orarioCorrente = orarioCorrente.plusMinutes(15);
        }

        return orariDisponibili;
    }


    // Metodo per verificare la sovrapposizione delle date e ore
    private boolean isSovrapposizione(LocalDateTime start, LocalDateTime end, Long prenotazioneId) {
        List<Prenotazione> prenotazioniEsistenti = prenotazioneRepository.findAll();

        // Controllo per vedere se la nuova prenotazione si sovrappone a quelle esistenti
        //In caso di modifica non tiene conto di quella che stiamo modificando
        //in caso di creazione non c'è nessun id quindi prenotazioneId = null
        for (Prenotazione prenotazione : prenotazioniEsistenti) {
            //In caso di modifica non tiene conto di quella che stiamo modificando
            //in caso di creazione non c'è nessuun id quindi salta questo if e controlla direttamente quelle esistenti
            if (prenotazioneId != null && prenotazione.getId().equals(prenotazioneId)) {
                continue;
            }
            LocalDateTime inizioEsistente = prenotazione.getDataOra();
            int durataServizio = prenotazione.getServizio().getDurata();
            LocalDateTime fineEsistente = inizioEsistente.plusMinutes(durataServizio);

            // Controllo per vedere se l'intervallo di tempo della nuova prenotazione si sovrappone con uno esistente
            if (start.isBefore(fineEsistente) && end.isAfter(inizioEsistente)) {
                return true; // c'è una sovrapposizione
            }
        }
        return false; // Nessuna sovrapposizione
    }

    public List<Prenotazione> getPrenotazioniByIndirizzo(Long indirizzoId) {
        Indirizzo indirizzo = indirizzoRepository.findById(indirizzoId)
                .orElseThrow(() -> new EntityNotFoundException("Indirizzo non trovato"));
        return prenotazioneRepository.findByIndirizzo(indirizzo);
    }

}
