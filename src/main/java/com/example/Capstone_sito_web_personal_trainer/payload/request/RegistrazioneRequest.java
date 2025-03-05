package com.example.Capstone_sito_web_personal_trainer.payload.request;

import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrazioneRequest {

    @NotBlank(message = "Il campo username è obblgatorio")
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank(message = "Il campo password è obblgatorio")
    @Size(min = 3, max = 15)
    private String password;

    @NotBlank(message = "Il campo nome è obblgatorio")
    private String nome;

    @NotBlank(message = "Il campo cognome è obblgatorio")
    private String cognome;

    @NotBlank(message = "Il campo email è obblgatorio")
    @Email(message = "Devi inserire un email valida")
    private String email;


    private UserRole ruolo;
}
