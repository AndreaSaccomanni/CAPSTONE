package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
import com.example.Capstone_sito_web_personal_trainer.payload.IndirizzoDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.IndirizzoMappperDTO;
import com.example.Capstone_sito_web_personal_trainer.repositories.IndirizzoRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.PrenotazioneRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class IndirizzoService {

    @Autowired
    IndirizzoRepository indirizzoRepository;

    @Autowired
    PrenotazioneRepository prenotazioneRepository;
    @Autowired
    IndirizzoMappperDTO indirizzoMapperDTO;

    public List<IndirizzoDTO> getAllIndirizzi() {
        return indirizzoRepository.findAll().stream().map(indirizzoMapperDTO::toDTO).collect(Collectors.toList());

    }

    public IndirizzoDTO getIndirizzoById(Long id) {
        Indirizzo indirizzo = indirizzoRepository.findById(id).orElseThrow(() -> new RuntimeException("Indirizzo non trovato"));
        return indirizzoMapperDTO.toDTO(indirizzo);
    }

    public IndirizzoDTO addIndirizzo(IndirizzoDTO indirizzoDto) {
        rimuoviGiorniGiaAssegnati(indirizzoDto.getGiorniDisponibili(), null);// null perchè non ha ancora l'id essendo un nuovo indirizzo

        Indirizzo nuovoIndirizzo = indirizzoMapperDTO.toEntity(indirizzoDto);
        indirizzoRepository.save(nuovoIndirizzo);
        return indirizzoMapperDTO.toDTO(nuovoIndirizzo);
    }

    public IndirizzoDTO updateIndirizzo(Long id, IndirizzoDTO indirizzoDto) {
        rimuoviGiorniGiaAssegnati(indirizzoDto.getGiorniDisponibili(), id);//non prende in consideraizione l'inndirizzo da aggiornare
        Indirizzo indirizzo = indirizzoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Indirizzo non trovato"));
        indirizzoMapperDTO.updateIndirizzo(indirizzoDto, indirizzo);
        indirizzoRepository.save(indirizzo);


        return indirizzoMapperDTO.toDTO(indirizzo);
    }

    public void deleteIndirizzo(Long id) {

        Indirizzo indirizzo = indirizzoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Indirizzo non trovato"));
        List<Prenotazione> prenotazioniCollegate = prenotazioneRepository.findByIndirizzo(indirizzo);

        // elimina prima le prenotazioni
        if (!prenotazioniCollegate.isEmpty()) {


            prenotazioneRepository.deleteAll(prenotazioniCollegate);
        }
        indirizzo.setGiorniDisponibili("");
        indirizzoRepository.save(indirizzo);
        // poi l'indirizzo
        indirizzoRepository.deleteById(id);
    }


    //metodo per rimuovere i giorni assegnati ad un indirizzo dai giorni disponibili degli altri indirizzi
    //non ci deve essere lo stesso giornoDIsponibile in due indirizzi diversi


    public void rimuoviGiorniGiaAssegnati(List<String> giorniAssegnati, Long idIndirizzoDaAggiornare) {
        List<Indirizzo> allIndirizzi = indirizzoRepository.findAll();
        for (Indirizzo i : allIndirizzi) {

            //salta l'indirizzo da aggiornare e prende in cosniderazione solo gli altri
            if (idIndirizzoDaAggiornare != null && i.getId().equals(idIndirizzoDaAggiornare))
                continue;//non prende in considerazione l'indirizzo che sto aggiornando, ma lo salto

            List<String> giorniDisponibili = i.getGiorniDisponibili() != null
                    ? Arrays.stream(i.getGiorniDisponibili().split(","))
                    .map(String::trim)
                    .toList()
                    : new ArrayList<>();

            //filtra solo i giorni disponibili per ogni indirizzo, tutti quelli che non stanno nell'array giorniAssegnti
            List<String> nuoviGiorniDisponibili = giorniDisponibili.stream().filter(giorno -> !giorniAssegnati.contains(giorno)).toList();

            //Se c'è qualche differenza quindi è stato tolto qualche giorni, salva le modifche
            if (nuoviGiorniDisponibili.size() != giorniDisponibili.size()) {
                i.setGiorniDisponibili(String.join(", ", nuoviGiorniDisponibili));
                indirizzoRepository.save(i);
            }
        }

    }
}
