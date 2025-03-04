package com.example.Capstone_sito_web_personal_trainer.payload;

import com.example.Capstone_sito_web_personal_trainer.enumeration.TipoConsulenza;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsulenzaDTO {
    private TipoConsulenza tipoConsulenza;
}
