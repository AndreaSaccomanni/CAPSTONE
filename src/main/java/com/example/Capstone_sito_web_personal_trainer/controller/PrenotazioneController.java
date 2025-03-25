package com.example.Capstone_sito_web_personal_trainer.controller;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
import com.example.Capstone_sito_web_personal_trainer.payload.PrenotazioneDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.request.CreaPrenotazioneRequest;
import com.example.Capstone_sito_web_personal_trainer.repositories.IndirizzoRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.PrenotazioneRepository;
import com.example.Capstone_sito_web_personal_trainer.service.PrenotazioneService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/prenotazioni")
public class PrenotazioneController {

    @Autowired
    PrenotazioneService prenotazioneService;



    //nuova prenotazione
    @PostMapping("/new")
    public ResponseEntity<?> creaPrenotazione(@RequestBody CreaPrenotazioneRequest prenotazioneDTO) {
        PrenotazioneDTO dto = prenotazioneService.creaPrenotazione(prenotazioneDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    //tutte le prenotazioni
    @GetMapping("/all")
    public ResponseEntity<List<PrenotazioneDTO>> getAllPrenotazioni() {
        List<PrenotazioneDTO> prenotazioni = prenotazioneService.getAllPrenotazioni();
        return ResponseEntity.ok(prenotazioni);
    }

    //prenotazione per ID
    @GetMapping("/{id}")
    public ResponseEntity<PrenotazioneDTO> getPrenotazioneById(@PathVariable Long id) {
        PrenotazioneDTO dto = prenotazioneService.getPrenotazioneById(id);
        return ResponseEntity.ok(dto);
    }

    // tutte le prenotazioni di un cliente
    @GetMapping("/utente/{utenteId}")
    public ResponseEntity<List<PrenotazioneDTO>> getPrenotazioniByUtente(@PathVariable Long utenteId) {
        List<PrenotazioneDTO> prenotazioni = prenotazioneService.getPrenotazioniByUtente(utenteId);
        return ResponseEntity.ok(prenotazioni);
    }

    // Cancella una prenotazione
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> cancellaPrenotazione(@PathVariable Long id) throws AccessDeniedException {
        prenotazioneService.cancellaPrenotazione(id);
        return ResponseEntity.noContent().build();
    }

    // Modifica una prenotazione
    @PutMapping("/update/{id}")
    public ResponseEntity<PrenotazioneDTO> modificaPrenotazione(@PathVariable Long id, @RequestBody PrenotazioneDTO prenotazioneDTO) throws AccessDeniedException {
        PrenotazioneDTO dto = prenotazioneService.modificaPrenotazione(id, prenotazioneDTO);
        return ResponseEntity.ok(dto);
    }

    // Orari disponibili nel giorno selezionato
    @GetMapping("/orariDisponibili")
    public ResponseEntity<List<LocalDateTime>> getOrariDisponibili(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam("servizioId") Long servizioId) {
        return ResponseEntity.ok(prenotazioneService.getOrariDisponibili(data, servizioId));
    }

    @GetMapping("/indirizzo/{id}")
    public List<Prenotazione> getByIndirizzo(@PathVariable Long id) {
        return prenotazioneService.getPrenotazioniByIndirizzo(id);
    }
}
