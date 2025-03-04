package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Consulenza;
import com.example.Capstone_sito_web_personal_trainer.payload.ConsulenzaDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.ConsulenzaMapperDTO;
import com.example.Capstone_sito_web_personal_trainer.repositories.ConsulenzaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsulenzaService {

    @Autowired
    ConsulenzaRepository consulenzaRepository;

    @Autowired
    ConsulenzaMapperDTO consulenzaMapperDTO;

    public Consulenza creaConsulenza(ConsulenzaDTO consulenzaDTO) {
        Consulenza consulenza = consulenzaMapperDTO.toEntity(consulenzaDTO);
        consulenza.setDurataConsulenza();
        return consulenzaRepository.save(consulenza);
    }

    public ConsulenzaDTO getConsulenzaById(Long id) {
        Consulenza consulenza = consulenzaRepository.findById(id).orElseThrow(() -> new RuntimeException("Consulenza non trovata"));
        return consulenzaMapperDTO.toDto(consulenza);
    }
}
