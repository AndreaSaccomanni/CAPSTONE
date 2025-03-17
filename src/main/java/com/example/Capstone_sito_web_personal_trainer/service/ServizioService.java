package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.Servizio;
import com.example.Capstone_sito_web_personal_trainer.repositories.ServizioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServizioService {

    @Autowired
    private ServizioRepository servizioRepository;

    public List<Servizio> getAllServizi() {
        return servizioRepository.findAll();
    }
}
