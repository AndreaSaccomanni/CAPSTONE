package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import com.example.Capstone_sito_web_personal_trainer.repositories.IndirizzoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class IndirizzoService {

    @Autowired
    IndirizzoRepository indirizzoRepository;

    public List<Indirizzo> getAllIndirizzi(){
        return indirizzoRepository.findAll();

    }

    public Indirizzo getIndirizzoById(Long id){
        return indirizzoRepository.findById(id).orElseThrow(() -> new RuntimeException("Indirizzo non trovato"));
    }

    public Indirizzo addIndirizzo(Indirizzo indirizzo){
        return indirizzoRepository.save(indirizzo);
    }

    public Indirizzo updateIndirizzo(Long id, Indirizzo nuovoIndirizzo) {
        Indirizzo indirizzo = getIndirizzoById(id);
        indirizzo.setVia(nuovoIndirizzo.getVia());
        indirizzo.setNumeroCivico(nuovoIndirizzo.getNumeroCivico());
        indirizzo.setCitta(nuovoIndirizzo.getCitta());
        indirizzo.setProvincia(nuovoIndirizzo.getProvincia());
        indirizzo.setLatitudine(nuovoIndirizzo.getLatitudine());
        indirizzo.setLongitudine(nuovoIndirizzo.getLongitudine());
        return indirizzoRepository.save(indirizzo);
    }

    public void deleteIndirizzo(Long id) {
        if (!indirizzoRepository.existsById(id)) {
            throw new EntityNotFoundException("Indirizzo non trovato ️ ️");
        }
        indirizzoRepository.deleteById(id);
    }
}
