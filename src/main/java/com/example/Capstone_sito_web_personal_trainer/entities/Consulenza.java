package com.example.Capstone_sito_web_personal_trainer.entities;

import com.example.Capstone_sito_web_personal_trainer.enumeration.TipoConsulenza;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@Entity
@DiscriminatorValue("CONSULENZA")

public class Consulenza extends Servizio{

    @Enumerated(EnumType.STRING)
    private TipoConsulenza tipoConsulenza;
    public void setDurataConsulenza() {
        switch (tipoConsulenza) {
            case VALUTAZIONE_INIZIALE:
                this.setDurata(30);
                break;
            case MIGLIORAMENTO_SCHEDA:
                this.setDurata(15);
                break;
            default:
                throw new IllegalArgumentException("Tipo di consulenza non valido: " + tipoConsulenza);
        }
    }

    @Override
    public String getNomeServizio() {
        return "Consulenza " + tipoConsulenza.name();
    }
}
