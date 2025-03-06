package com.example.Capstone_sito_web_personal_trainer.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MailModel {

    @Email(message = "Formato email non valido")
    @NotNull(message = "Il campo 'destinatario' non può essere vuoto")
    private String destinatario;

    @NotNull(message = "Il campo 'oggetto' non può essere vuoto")
    private String oggetto;

    @NotNull(message = "Il campo 'contenuto' non può essere vuoto")
    private String contenuto;
}//