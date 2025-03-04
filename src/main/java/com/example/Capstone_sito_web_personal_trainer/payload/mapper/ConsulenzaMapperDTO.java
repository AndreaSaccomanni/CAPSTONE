package com.example.Capstone_sito_web_personal_trainer.payload.mapper;

import com.example.Capstone_sito_web_personal_trainer.entities.Consulenza;
import com.example.Capstone_sito_web_personal_trainer.payload.ConsulenzaDTO;
import org.springframework.stereotype.Component;

@Component
public class ConsulenzaMapperDTO {


    public ConsulenzaDTO toDto(Consulenza consulenza) {
        ConsulenzaDTO dto = new ConsulenzaDTO();
        dto.setTipoConsulenza(consulenza.getTipoConsulenza());
        return dto;
    }


    public Consulenza toEntity(ConsulenzaDTO consulenzaDTO) {
        Consulenza consulenza = new Consulenza();
        consulenza.setTipoConsulenza(consulenzaDTO.getTipoConsulenza());
        consulenza.setDurataConsulenza();
        return consulenza;
    }}

