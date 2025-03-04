package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import com.example.Capstone_sito_web_personal_trainer.entities.Servizio;
import com.example.Capstone_sito_web_personal_trainer.payload.PrenotazioneDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.PrenotazioneMapperDTO;
import com.example.Capstone_sito_web_personal_trainer.repositories.PrenotazioneRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.ServizioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public PrenotazioneDTO creaPrenotazione(PrenotazioneDTO prenotazioneDTO) {
        // Recupero il servizio
        Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato!⚠️"));

        // Recupero l'utente
        Utente utente = utenteRepository.findById(prenotazioneDTO.getUtenteId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato!"));

        int durataServizio = servizio.getDurata(); // Durata del servizio in minuti
        LocalDateTime start = prenotazioneDTO.getDataOraPrenotazione();
        LocalDateTime end = start.plusMinutes(durataServizio); // Calcola l'orario di fine

        // Controllo per vedere se ci sono sovrapposizioni con altre prenotazioni
        if (isSovrapposizione(start, end)) {
            throw new IllegalArgumentException("Impossibile creare una prenotazione per l'ora e la data selezionata⚠️");
        }

        // Creo la nuova prenotazione
        Prenotazione prenotazione = prenotazioneMapperDTO.toEntity(prenotazioneDTO);
        prenotazione.setUtente(utente);
        prenotazione.setServizio(servizio);
        prenotazione.setDataOra(start);

        // Salvo la prenotazione
        prenotazione = prenotazioneRepository.save(prenotazione);

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

    public void cancellaPrenotazione(Long id) {
        if (!prenotazioneRepository.existsById(id)) {
            throw new EntityNotFoundException("Prenotazione non trovata!");
        }
        prenotazioneRepository.deleteById(id);
    }

    public PrenotazioneDTO modificaPrenotazione(Long id, PrenotazioneDTO prenotazioneDTO) {
        //Recupero la prenotazione nel db
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata!⚠️"));

        // PER MODIFICARE LA DATA DI UNA PRENOTAZIONE BISOGNA INSERIRE ANCHE L'ID DEL SERVIZIO
        if (prenotazioneDTO.getDataOraPrenotazione() != null) {
            // Recupera il servizio scelto e la durata
            Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                    .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato!⚠️"));
            prenotazione.setServizio(servizio);

            int durataServizio = servizio.getDurata(); // Durata del servizio in minuti
            LocalDateTime start = prenotazioneDTO.getDataOraPrenotazione();
            LocalDateTime end = start.plusMinutes(durataServizio); // Calcola l'orario di fine

            // Controllo per vedere se ci sono sovrapposizioni con altre prenotazioni
            if (isSovrapposizione(start, end)) {
                throw new IllegalArgumentException("La prenotazione si sovrappone con una già esistente.");
            }

            prenotazione.setDataOra(start); // Aggiorna la data e ora della prenotazione
        }

        // Se viene fornito un nuovo servizio, aggiorna
        if (prenotazioneDTO.getServizioId() != null) {
            Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                    .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato!⚠️"));
            prenotazione.setServizio(servizio);
        }

        // Se viene fornito un nuovo utente, aggiorna
        if (prenotazioneDTO.getUtenteId() != null) {
            Utente utente = utenteRepository.findById(prenotazioneDTO.getUtenteId())
                    .orElseThrow(() -> new EntityNotFoundException("Utente non trovato!⚠️"));
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
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato!"));

        List<Prenotazione> prenotazioni = prenotazioneRepository.findByUtente(utente);
        return prenotazioni.stream()
                .map(prenotazioneMapperDTO::toDto)
                .collect(Collectors.toList());
    }

    // Metodo per verificare la sovrapposizione delle date e ore
    private boolean isSovrapposizione(LocalDateTime start, LocalDateTime end) {
        List<Prenotazione> prenotazioniEsistenti = prenotazioneRepository.findAll();

        // Verifica se la nuova prenotazione si sovrappone a quelle esistenti
        for (Prenotazione prenotazione : prenotazioniEsistenti) {
            LocalDateTime inizioEsistente = prenotazione.getDataOra();
            int durataServizio = prenotazione.getServizio().getDurata();
            LocalDateTime fineEsistente = inizioEsistente.plusMinutes(durataServizio);

            // Controlla se l'intervallo di tempo della nuova prenotazione si sovrappone con uno esistente
            if (start.isBefore(fineEsistente) && end.isAfter(inizioEsistente)) {
                return true; // C'è sovrapposizione
            }
        }
        return false; // Nessuna sovrapposizione
    }


}
