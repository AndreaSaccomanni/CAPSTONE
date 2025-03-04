package com.example.Capstone_sito_web_personal_trainer.payload.mapper;

import com.example.Capstone_sito_web_personal_trainer.entities.Massaggio;
import com.example.Capstone_sito_web_personal_trainer.payload.MassaggioDTO;
import org.springframework.stereotype.Component;

@Component
public class MassaggioMapperDTO {
    public MassaggioDTO toDto(Massaggio massaggio) {
        MassaggioDTO dto = new MassaggioDTO();
        dto.setTipoMassaggio(massaggio.getTipoMassaggio()); // Copia il tipo di massaggio
        return dto;
    }

    public Massaggio toEntity(MassaggioDTO dto){
        Massaggio entity = new Massaggio();
        entity.setTipoMassaggio(dto.getTipoMassaggio());
        entity.setDurataMassaggio();
        return entity;
    }
}
