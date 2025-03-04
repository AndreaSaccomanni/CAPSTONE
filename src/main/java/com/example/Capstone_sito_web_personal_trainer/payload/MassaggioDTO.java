package com.example.Capstone_sito_web_personal_trainer.payload;

import com.example.Capstone_sito_web_personal_trainer.enumeration.TipoMassaggio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MassaggioDTO {

    private TipoMassaggio tipoMassaggio;
}

