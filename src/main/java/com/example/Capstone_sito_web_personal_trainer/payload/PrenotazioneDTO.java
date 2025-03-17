package com.example.Capstone_sito_web_personal_trainer.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrenotazioneDTO {

    private Long prenotazioneId;
    private Long utenteId;
    @NotNull
    private Long servizioId;
    @NotNull
    private LocalDateTime dataOraPrenotazione;
    private String note;

    private String nomeServizio;
    private String nomeUtente;
    private String cognomeUtente;
}
