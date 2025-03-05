package com.example.Capstone_sito_web_personal_trainer.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MailModel {

    @Email(message = "Formato email non valido")
    @NotNull(message = "Il destinatario non può essere nullo")
    private String destinatario;

    @NotNull(message = "L'oggetto non può essere nullo")
    private String oggetto;

    @NotNull(message = "Il contenuto non può essere nullo")
    private String contenuto;
}
