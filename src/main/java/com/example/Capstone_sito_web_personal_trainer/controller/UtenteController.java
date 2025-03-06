package com.example.Capstone_sito_web_personal_trainer.controller;

import com.example.Capstone_sito_web_personal_trainer.payload.UtenteDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.request.LoginRequest;
import com.example.Capstone_sito_web_personal_trainer.payload.response.JwtResponse;
import com.example.Capstone_sito_web_personal_trainer.security.jwt.JwtUtils;
import com.example.Capstone_sito_web_personal_trainer.security.services.UserDetailsImpl;
import com.example.Capstone_sito_web_personal_trainer.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utenti")
public class UtenteController {

    @Autowired
    UtenteService utenteService;

    @Autowired
    JwtUtils jwtUtils;


    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/registrazione")
    public ResponseEntity<UtenteDTO> registraUtente(@RequestBody UtenteDTO utenteDTO) throws InterruptedException {
        UtenteDTO dto = utenteService.registraUtente(utenteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginDto, BindingResult validazione) {
        if (validazione.hasErrors()) {
            String messaggioErrore = "ERRORE DI VALIDAZIONE";
            for (ObjectError errore : validazione.getAllErrors()) {
                messaggioErrore += errore.getDefaultMessage() + "\n";
            }
            return new ResponseEntity<>(messaggioErrore, HttpStatus.BAD_REQUEST);
        }

        // Creazione dell'oggetto per l'autenticazione
        UsernamePasswordAuthenticationToken tokenNOAuthentication =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // Autenticazione dell'utente
        Authentication autenticazione = authenticationManager.authenticate(tokenNOAuthentication);

        // Imposta l'autenticazione nel contesto di sicurezza
        SecurityContextHolder.getContext().setAuthentication(autenticazione);

        // Generazione del token JWT
        String token = jwtUtils.creaJwtToken(autenticazione);

        // Recupero dei dettagli dell'utente autenticato
        UserDetailsImpl dettagliUtente = (UserDetailsImpl) autenticazione.getPrincipal();

        // Recupero del ruolo dell'utente come stringa
        String ruoloWeb = dettagliUtente.getRuolo().name();

        // Creazione della risposta JWT
        JwtResponse responseJwt = new JwtResponse(
                dettagliUtente.getUsername(),
                dettagliUtente.getId(),
                dettagliUtente.getEmail(),
                ruoloWeb,  // ← RUOLO SINGOLO
                token
        );

        return new ResponseEntity<>(responseJwt, HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<List<UtenteDTO>> getAllUtenti(){
        List<UtenteDTO> utenti = utenteService.getAllUtenti();
        return ResponseEntity.ok(utenti);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtenteDTO> getUtenteById(@PathVariable Long id){
        return ResponseEntity.ok(utenteService.getUtenteById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUtente(@PathVariable Long id){
        utenteService.deleteUtente(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UtenteDTO> updateUtente(@PathVariable Long id, @RequestBody UtenteDTO utenteDTO){
        return ResponseEntity.ok(utenteService.updateUtente(id, utenteDTO));
    }
}
