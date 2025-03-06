package com.example.Capstone_sito_web_personal_trainer.entities;

import com.example.Capstone_sito_web_personal_trainer.enumeration.TipoMassaggio;
import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@DiscriminatorValue("MASSAGGIO")
public class Massaggio extends Servizio {

    @Enumerated(EnumType.STRING)
    private TipoMassaggio tipoMassaggio;

    public void setDurataMassaggio() {
        switch (tipoMassaggio) {
            case RILASSANTE:
                this.setDurata(60);
                break;
            case DECONTRATTURANTE_SPORTIVO:
                this.setDurata(30);
                break;
            default:
                throw new IllegalArgumentException("Tipo di massaggio non valido: " + tipoMassaggio);
        }


}

    @Override
    public String getNomeServizio() {
        return "Massaggio " + tipoMassaggio.name();
    }
}
