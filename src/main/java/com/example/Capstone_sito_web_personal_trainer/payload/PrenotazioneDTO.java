package com.example.Capstone_sito_web_personal_trainer.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrenotazioneDTO {
    private Long id;
    private Long utenteId;
    private Long servizioId;
    private LocalDateTime dataOraPrenotazione;
    private String note;
}
