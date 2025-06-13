package com.example.Capstone_sito_web_personal_trainer.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormInformazioniDTO {
    private String nome;
    private String cognome;
    private String email;  //email dell'utente (mittente)
    private String messaggio;
}
