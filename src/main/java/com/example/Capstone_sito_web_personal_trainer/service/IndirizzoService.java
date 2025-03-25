package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
//import com.example.Capstone_sito_web_personal_trainer.enumeration.GiorniDisponibili;
import com.example.Capstone_sito_web_personal_trainer.repositories.IndirizzoRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.PrenotazioneRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class IndirizzoService {

    @Autowired
    IndirizzoRepository indirizzoRepository;

    @Autowired
    PrenotazioneRepository prenotazioneRepository;

    public List<Indirizzo> getAllIndirizzi(){
        return indirizzoRepository.findAll();

    }

    public Indirizzo getIndirizzoById(Long id){
        return indirizzoRepository.findById(id).orElseThrow(() -> new RuntimeException("Indirizzo non trovato"));
    }

    public Indirizzo addIndirizzo(Indirizzo indirizzo){
//        rimuoviGiorniGiaAssegnati(indirizzo.getGiorniDisponibili(), null);// null perchè non ha ancora l'id essendo un nuovo indirizzo
        return indirizzoRepository.save(indirizzo);
    }

    public Indirizzo updateIndirizzo(Long id, Indirizzo nuovoIndirizzo) {
//        rimuoviGiorniGiaAssegnati(nuovoIndirizzo.getGiorniDisponibili(), id);//non prende in consideraizione l'inndirizzo da aggiornare
        Indirizzo indirizzo = getIndirizzoById(id);
        indirizzo.setVia(nuovoIndirizzo.getVia());
        indirizzo.setNumeroCivico(nuovoIndirizzo.getNumeroCivico());
        indirizzo.setCitta(nuovoIndirizzo.getCitta());
        indirizzo.setProvincia(nuovoIndirizzo.getProvincia());
        indirizzo.setLatitudine(nuovoIndirizzo.getLatitudine());
        indirizzo.setLongitudine(nuovoIndirizzo.getLongitudine());
        indirizzo.setNomeStudio(nuovoIndirizzo.getNomeStudio());

        return indirizzoRepository.save(indirizzo);
    }

    public void deleteIndirizzo(Long id) {

        Indirizzo indirizzo = indirizzoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Indirizzo non trovato"));
        List<Prenotazione> prenotazioniCollegate = prenotazioneRepository.findByIndirizzo(indirizzo);

        // elimina prima le prenotazioni
        if (!prenotazioniCollegate.isEmpty()) {
            prenotazioneRepository.deleteAll(prenotazioniCollegate);
        }
        // poi l'indirizzo
        indirizzoRepository.deleteById(id);
    }


    //metodo per rimuovere i giorni assegnati ad un indirizzo dai giorni disponibili degli altri indirizzi
    //non ci deve essere lo stesso giornoDIsponibile in due indirizzi diversi
//    public void rimuoviGiorniGiaAssegnati(List<GiorniDisponibili>giorniAssegnati, Long idIndirizzoDaAggiornare){
//        List<Indirizzo> allIndirizzi = indirizzoRepository.findAll();
//        for(Indirizzo i : allIndirizzi){
//            //salta l'indirizzo da aggiornare e prende in cosniderazione solo gli altri
//            if(idIndirizzoDaAggiornare != null && i.getId().equals(idIndirizzoDaAggiornare)) continue;
//
//            //filtra solo i giorni disponibili per ogni indirizzo, tutti quelli che non stanno nell'array giorniAssegnti
//            List<GiorniDisponibili> giorniDisponibili = new ArrayList<>(
//                    i.getGiorniDisponibili().stream()
//                            .filter(giorno -> !giorniAssegnati.contains(giorno))
//                            .toList()
//            );
//
//            //Se c'è qualche differenza quindi è stato tolto qualche giorni, salva le modifche
//            if(giorniDisponibili.size() != i.getGiorniDisponibili().size()){
//                i.setGiorniDisponibili(giorniDisponibili);
//                indirizzoRepository.save(i);
//            }
//        }

 //   }
}
