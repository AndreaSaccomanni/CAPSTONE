package com.example.Capstone_sito_web_personal_trainer.payload.mapper;

import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import com.example.Capstone_sito_web_personal_trainer.payload.UtenteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UtenteMapperDTO {

    @Autowired
    PasswordEncoder passwordEncoder;

    public UtenteDTO toDto(Utente entity) {
        UtenteDTO dto = new UtenteDTO();

        dto.setNome(entity.getNome());
        dto.setCognome(entity.getCognome());
        dto.setDataDiNascita(entity.getDataDiNascita());
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());


        if (entity.getRuolo() != null) {
            dto.setRuolo(entity.getRuolo().name()); // Converte l'enum in stringa
        }

        return dto;
    }

    public Utente toEntity(UtenteDTO dto) {
        Utente entity = new Utente();
        entity.setNome(dto.getNome());
        entity.setCognome(dto.getCognome());
        entity.setDataDiNascita(dto.getDataDiNascita());
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRuolo() != null) {
            entity.setRuolo(UserRole.valueOf(dto.getRuolo().toUpperCase())); // Converte la stringa in enum
        }
        return entity;
    }

    public Utente updateUtente(UtenteDTO utenteDTO, Utente utente){
        if(utenteDTO.getNome() != null){
            utente.setNome(utenteDTO.getNome());
        }
        if(utenteDTO.getCognome() != null){
            utente.setCognome(utenteDTO.getCognome());
        }
        if(utenteDTO.getDataDiNascita() != null){
            utente.setDataDiNascita(utenteDTO.getDataDiNascita());
        }
        if(utenteDTO.getUsername()!= null){
            utente.setUsername(utenteDTO.getUsername());
        }
        if (utenteDTO.getEmail() != null) {
            utente.setEmail(utenteDTO.getEmail());
        }
        if (utenteDTO.getPassword() != null && !utenteDTO.getPassword().isBlank()) {
            utente.setPassword(passwordEncoder.encode(utenteDTO.getPassword()));
        }
        if(utenteDTO.getRuolo() != null){
            utente.setRuolo(UserRole.valueOf(utenteDTO.getRuolo()));
        }

        return utente;
    }
}
