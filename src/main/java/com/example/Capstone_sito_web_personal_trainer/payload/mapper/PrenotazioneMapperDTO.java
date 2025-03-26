package com.example.Capstone_sito_web_personal_trainer.payload.mapper;

import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
import com.example.Capstone_sito_web_personal_trainer.payload.PrenotazioneDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.request.CreaPrenotazioneRequest;
import com.example.Capstone_sito_web_personal_trainer.repositories.IndirizzoRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.ServizioRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrenotazioneMapperDTO {

    @Autowired
    ServizioRepository servizioRepository;

    @Autowired
    UtenteRepository utenteRepository;

    @Autowired
    IndirizzoRepository indirizzoRepository;
    @Autowired
    IndirizzoMappperDTO indirizzoMapper;

    public PrenotazioneDTO toDto(Prenotazione entity) {
        PrenotazioneDTO dto = new PrenotazioneDTO();
        dto.setPrenotazioneId(entity.getId());
        dto.setUtenteId(entity.getUtente().getId());
        dto.setServizioId(entity.getServizio().getId());
        dto.setIndirizzoId(entity.getIndirizzo().getId());
        dto.setIndirizzo(indirizzoMapper.toDTO(entity.getIndirizzo()));
        dto.setDataOraPrenotazione(entity.getDataOra());
        dto.setNote(entity.getNote());
        dto.setNomeUtente(entity.getUtente().getNome());
        dto.setCognomeUtente(entity.getUtente().getCognome());
        dto.setNomeServizio(entity.getServizio().getNomeServizio());
        return dto;
    }

    public Prenotazione toEntity(PrenotazioneDTO dto) {
        Prenotazione entity = new Prenotazione();
        entity.setUtente(utenteRepository.findById(dto.getUtenteId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato!")));
        entity.setServizio(servizioRepository.findById(dto.getServizioId())
                .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato")));
        entity.setIndirizzo(indirizzoRepository.findById(dto.getIndirizzoId())
                .orElseThrow(() -> new EntityNotFoundException("Indirizzo non trovato")));
        entity.setDataOra(dto.getDataOraPrenotazione());
        entity.setNote(dto.getNote());
        return entity;
    }

    public Prenotazione requestToEntity(CreaPrenotazioneRequest dto){
        Prenotazione entity = new Prenotazione();
        entity.setServizio(servizioRepository.findById(dto.getServizioId())
                .orElseThrow(() -> new EntityNotFoundException("Servizio non trovato")));
        entity.setIndirizzo(indirizzoRepository.findById(dto.getIndirizzoId())
                .orElseThrow(() -> new EntityNotFoundException("Indirizo non trovato")));
        entity.setDataOra(dto.getDataOraPrenotazione());
        entity.setNote(dto.getNote());
        return entity;
    }

}
