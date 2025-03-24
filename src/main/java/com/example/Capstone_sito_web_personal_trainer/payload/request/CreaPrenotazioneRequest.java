package com.example.Capstone_sito_web_personal_trainer.payload.request;


import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreaPrenotazioneRequest {

    private Long utenteId;
    private Long servizioId;
    private LocalDateTime dataOraPrenotazione;
    private String note;
    private Long indirizzoId;


}

