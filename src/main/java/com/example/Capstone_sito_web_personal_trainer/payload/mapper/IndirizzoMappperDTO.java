package com.example.Capstone_sito_web_personal_trainer.payload.mapper;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import com.example.Capstone_sito_web_personal_trainer.payload.IndirizzoDTO;
import org.springframework.stereotype.Component;

@Component
public class IndirizzoMappperDTO {

    public IndirizzoDTO toDTO(Indirizzo entity){
        IndirizzoDTO indirizzo = new IndirizzoDTO();
        indirizzo.setId(entity.getId());
        indirizzo.setVia(entity.getVia());
        indirizzo.setNumeroCivico(entity.getNumeroCivico());
        indirizzo.setCitta(entity.getCitta());
        indirizzo.setProvincia(entity.getProvincia());
        indirizzo.setProvincia(entity.getProvincia());
        indirizzo.setNomeStudio(entity.getNomeStudio());
        indirizzo.setLongitudine(entity.getLongitudine());
        indirizzo.setLatitudine(entity.getLatitudine());
        return indirizzo;
    }



    public Indirizzo toEntity(IndirizzoDTO dto){
        Indirizzo indirizzo = new Indirizzo();
        indirizzo.setId(dto.getId());
        indirizzo.setVia(dto.getVia());
        indirizzo.setNumeroCivico(dto.getNumeroCivico());
        indirizzo.setCitta(dto.getCitta());
        indirizzo.setProvincia(dto.getProvincia());
        indirizzo.setNomeStudio(dto.getNomeStudio());
        indirizzo.setLongitudine(dto.getLongitudine());
        indirizzo.setLatitudine(dto.getLatitudine());
        return indirizzo;
    }

    public Indirizzo updateIndirizzo(IndirizzoDTO indirizzoDTO, Indirizzo indirizzo){
        if(indirizzoDTO.getId() != null){
            indirizzo.setId(indirizzoDTO.getId());
        }
        if(indirizzoDTO.getVia() != null){
            indirizzo.setVia(indirizzoDTO.getVia());
        }
        if(indirizzoDTO.getNumeroCivico() != null){
            indirizzo.setNumeroCivico(indirizzoDTO.getNumeroCivico());
        }
        if(indirizzoDTO.getCitta() != null){
            indirizzo.setCitta(indirizzoDTO.getCitta());
        }
        if(indirizzoDTO.getProvincia() != null){
            indirizzo.setProvincia(indirizzoDTO.getProvincia());
        }
        if(indirizzoDTO.getNomeStudio() != null){
            indirizzo.setNomeStudio(indirizzoDTO.getNomeStudio());
        }
        if(indirizzoDTO.getLongitudine() != null){
            indirizzo.setLongitudine(indirizzoDTO.getLongitudine());
        }
        if(indirizzoDTO.getLatitudine() != null){
            indirizzo.setLatitudine(indirizzoDTO.getLatitudine());
        }

        return indirizzo;

    }
}
