package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Massaggio;
import com.example.Capstone_sito_web_personal_trainer.payload.MassaggioDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.MassaggioMapperDTO;
import com.example.Capstone_sito_web_personal_trainer.repositories.MassaggioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MassaggioService {

    @Autowired
    private MassaggioRepository massaggioRepository;

    @Autowired
    private MassaggioMapperDTO massaggioMapperDTO;

    public Massaggio creaMassaggio(MassaggioDTO massaggioDTO) {
        Massaggio massaggio = massaggioMapperDTO.toEntity(massaggioDTO);
        massaggio.setDurataMassaggio();
        return massaggioRepository.save(massaggio);
    }

    public MassaggioDTO getMassaggioById(Long id) {
        Massaggio massaggio = massaggioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Massaggio non trovato"));
        return massaggioMapperDTO.toDto(massaggio);
    }
}
