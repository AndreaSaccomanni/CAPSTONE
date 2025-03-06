package com.example.Capstone_sito_web_personal_trainer.controller;

import com.example.Capstone_sito_web_personal_trainer.entities.MailModel;
import com.example.Capstone_sito_web_personal_trainer.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    MailService mailService;

    @PostMapping("/sendMail")
    public ResponseEntity<String> inviaMail(@RequestBody @Validated MailModel mailModel, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Errore di validazione: " + bindingResult.getAllErrors().toString());
        }

        // Se la validazione va a buon fine, invia la mail
        String messaggio = mailService.inviaEmail(mailModel);
        return ResponseEntity.ok(messaggio);
    }
}
