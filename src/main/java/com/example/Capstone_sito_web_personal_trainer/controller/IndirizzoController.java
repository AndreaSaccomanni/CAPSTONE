package com.example.Capstone_sito_web_personal_trainer.controller;


import com.example.Capstone_sito_web_personal_trainer.payload.IndirizzoDTO;
import com.example.Capstone_sito_web_personal_trainer.service.IndirizzoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/indirizzi")
public class IndirizzoController {

    @Autowired
    IndirizzoService indirizzoService;

    @PostMapping("/new")
    public ResponseEntity<IndirizzoDTO> creaIndirizzo(@RequestBody IndirizzoDTO indirizzoDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(indirizzoService.addIndirizzo(indirizzoDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<IndirizzoDTO>> getAllIndirizzi() {
        List<IndirizzoDTO> indirizzi = indirizzoService.getAllIndirizzi();
        return ResponseEntity.status(HttpStatus.OK).body(indirizzi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndirizzoDTO> getIndirizzoById(@PathVariable Long id) {

        return ResponseEntity.ok(indirizzoService.getIndirizzoById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<IndirizzoDTO> updateIndirizzo(@PathVariable Long id, @RequestBody IndirizzoDTO nuovoIndirizzo) {

        return ResponseEntity.status(HttpStatus.OK).body(indirizzoService.updateIndirizzo(id, nuovoIndirizzo));

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteIndirizzo(@PathVariable Long id) {
        indirizzoService.deleteIndirizzo(id);
        return ResponseEntity.noContent().build();
    }
}
