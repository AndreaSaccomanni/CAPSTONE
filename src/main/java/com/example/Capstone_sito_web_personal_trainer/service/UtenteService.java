package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import com.example.Capstone_sito_web_personal_trainer.exception.EmailDuplicateException;
import com.example.Capstone_sito_web_personal_trainer.exception.UsernameDuplicateException;
import com.example.Capstone_sito_web_personal_trainer.payload.UtenteDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.UtenteMapperDTO;
import com.example.Capstone_sito_web_personal_trainer.repositories.PrenotazioneRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtenteService {
    @Autowired
    UtenteRepository utenteRepository;

    @Autowired
    UtenteMapperDTO utenteMapperDTO;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PrenotazioneRepository prenotazioneRepository;

    public UtenteDTO registraUtente(UtenteDTO utenteDTO) throws InterruptedException{

        checkDuplicateKey(utenteDTO.getUsername(), utenteDTO.getEmail());

        Utente utente = utenteMapperDTO.toEntity(utenteDTO);

        //Imposto USER come ruolo di default
        if(utente.getRuolo() == null){
            utente.setRuolo(UserRole.USER);
        }

        utenteRepository.save(utente);

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

        Utente utente = utenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato!⚠️"));

        prenotazioneRepository.deleteAll(utente.getPrenotazioni());

        utenteRepository.deleteById(id);
    }

    public UtenteDTO updateUtente(Long id, UtenteDTO utenteDTO) {
        Utente utente = utenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato!⚠️"));
        utente = utenteMapperDTO.updateUtente(utenteDTO, utente);
        utente = utenteRepository.save(utente);
        return utenteMapperDTO.toDto(utente);
    }

    // Controllo per vedere se email o username sono già presenti nel sistema
    public void checkDuplicateKey(String username, String email) throws UsernameDuplicateException, EmailDuplicateException {
        if (utenteRepository.existsByEmail(email)) {
            throw new EmailDuplicateException("Email già presente nel sistema");
        }
        if (utenteRepository.existsByUsername(username)) {
            throw new UsernameDuplicateException("Username già presente nel sistema");
        }
    }



}
