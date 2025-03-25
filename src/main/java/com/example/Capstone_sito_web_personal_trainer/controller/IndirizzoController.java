package com.example.Capstone_sito_web_personal_trainer.controller;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
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
    public ResponseEntity<?> creaIndirizzo(@RequestBody Indirizzo indirizzo){
        indirizzoService.addIndirizzo(indirizzo);
        return  ResponseEntity.status(HttpStatus.CREATED).body(indirizzo);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Indirizzo>> getAllIndirizzi(){
        List<Indirizzo> indirizzi = indirizzoService.getAllIndirizzi();
        return ResponseEntity.status(HttpStatus.OK).body(indirizzi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIndirizzoById(@PathVariable Long id){
        Indirizzo indirizzo = indirizzoService.getIndirizzoById(id);
        return ResponseEntity.status(HttpStatus.OK).body(indirizzo);
    }

    @PutMapping("/update/{id}")
    public  ResponseEntity<?> updateIndirizzo(@PathVariable Long id,  @RequestBody Indirizzo nuovoIndirizzo){
        indirizzoService.updateIndirizzo(id, nuovoIndirizzo);
        return  ResponseEntity.status(HttpStatus.OK).body(nuovoIndirizzo);

    }

    @DeleteMapping("/delete/{id}")
    public  ResponseEntity<?> deleteIndirizzo(@PathVariable Long id){
        indirizzoService.deleteIndirizzo(id);
        return ResponseEntity.noContent().build();
    }
}
