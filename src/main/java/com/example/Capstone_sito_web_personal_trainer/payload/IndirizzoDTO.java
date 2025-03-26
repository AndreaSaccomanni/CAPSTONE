package com.example.Capstone_sito_web_personal_trainer.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndirizzoDTO {
    private Long id;

    @NotNull
    private String via;
    @NotNull
    private String numeroCivico;
    @NotNull
    private String citta;
    @NotNull
    private String provincia;

    private Double latitudine;

    private Double longitudine;
    @NotNull
    private String nomeStudio;
}
