package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import com.example.Capstone_sito_web_personal_trainer.payload.UtenteDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.UtenteMapperDTO;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtenteService {
    @Autowired
    UtenteRepository utenteRepository;

    @Autowired
    UtenteMapperDTO utenteMapperDTO;

    public UtenteDTO registraUtente(UtenteDTO utenteDTO) throws InterruptedException{
        if(utenteRepository.existsByEmail(utenteDTO.getEmail())){
            throw new IllegalStateException("Email gia in uso!⚠️");
        }

        Utente utente = utenteMapperDTO.toEntity(utenteDTO);

        //Imposto USER come ruolo di default
        if(utente.getRuolo() == null){
            utente.setRuolo(UserRole.USER);
        }

        utenteRepository.save(utente);

//        utenteRepository.flush();
//        Thread.sleep(200);

        // Mapper from utente to dto
        return utenteMapperDTO.toDto(utente);
    }

    public UtenteDTO getUtenteById(Long id){
        Utente utente = utenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato!⚠️"));
        return utenteMapperDTO.toDto(utente);
    }

    public List<UtenteDTO> getAllUtenti(){
        List<Utente> utenti = utenteRepository.findAll();
        return utenti.stream().map(utenteMapperDTO::toDto).collect(Collectors.toList());
    }

    public void deleteUtente(Long id){
        if(!utenteRepository.existsById(id)){
            throw new EntityNotFoundException("Utente non trovato!⚠️");
        }
        utenteRepository.deleteById(id);
    }

    public UtenteDTO updateUtente(Long id, UtenteDTO utenteDTO) {
        Utente utente = utenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato!⚠️"));
        utente = utenteMapperDTO.updateUtente(utenteDTO, utente);
        utente = utenteRepository.save(utente);
        return utenteMapperDTO.toDto(utente);
    }



}
