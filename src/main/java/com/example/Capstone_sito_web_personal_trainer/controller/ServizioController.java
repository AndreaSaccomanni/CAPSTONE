package com.example.Capstone_sito_web_personal_trainer.controller;

import com.example.Capstone_sito_web_personal_trainer.entities.Servizio;
import com.example.Capstone_sito_web_personal_trainer.service.ServizioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servizi")
public class ServizioController {

    @Autowired
    private ServizioService servizioService;

    @GetMapping("/all")
    public ResponseEntity<List<Servizio>> getAllServizi() {
        List<Servizio> servizi = servizioService.getAllServizi();
        return ResponseEntity.ok(servizi);
    }
}
