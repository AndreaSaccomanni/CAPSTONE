package com.example.Capstone_sito_web_personal_trainer.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtenteDTO {

    @NotBlank(message = "Il campo 'nome' non può essere vuoto")
    private String nome;

    @NotBlank(message = "Il campo 'cognome' non può essere vuoto")
    private String cognome;

    private LocalDate dataDiNascita;

    @NotBlank(message = "Il campo 'username' non può essere vuoto")
    private String username;

    @NotBlank(message = "Il campo 'email' non può essere vuoto")
    @Email(message = "Inserire una email valida")
    private String email;


    @NotBlank(message = "Il campo password è obblgatorio")
    @Size(min = 3)
    private String password;

    private String ruolo;
}
