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

import java.util.List;
import java.util.Optional;
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

        if (prenotazioneRepository.existsByDataOra(prenotazioneDTO.getDataOraPrenotazione())) {
            throw new IllegalArgumentException("Esiste già una prenotazione per questa data e ora.");
        }
        Prenotazione prenotazione = prenotazioneMapperDTO.toEntity(prenotazioneDTO);

        prenotazione = prenotazioneRepository.save(prenotazione);

        return prenotazioneMapperDTO.toDto(prenotazione);
    }

    public PrenotazioneDTO getPrenotazioneById(Long id){
        Prenotazione prenotazione = prenotazioneRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata"));
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
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Prenotazione non trovata!⚠️"));

        if (prenotazioneDTO.getServizioId() != null) {
            Servizio servizio = servizioRepository.findById(prenotazioneDTO.getServizioId())
                    .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato!⚠️"));
            prenotazione.setServizio(servizio);
        }

        if(prenotazioneDTO.getUtenteId() != null){
            Utente utente = utenteRepository.findById(prenotazioneDTO.getUtenteId())
                    .orElseThrow(() -> new EntityNotFoundException("Utente non trovato!"));
            prenotazione.setUtente(utente);
        }

        if (prenotazioneDTO.getDataOraPrenotazione() != null) {
            prenotazione.setDataOra(prenotazioneDTO.getDataOraPrenotazione());
        }

        prenotazione = prenotazioneRepository.save(prenotazione);
        return prenotazioneMapperDTO.toDto(prenotazione);
    }

}

