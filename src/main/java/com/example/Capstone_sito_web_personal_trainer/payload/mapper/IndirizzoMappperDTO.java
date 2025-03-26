package com.example.Capstone_sito_web_personal_trainer.payload.mapper;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import com.example.Capstone_sito_web_personal_trainer.payload.IndirizzoDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class IndirizzoMappperDTO {

    public IndirizzoDTO toDTO(Indirizzo entity) {
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
        //da una stringa ottengo un array, usando come separatore la virgola per identidicare i vari elementi dell'array --> "LUNEDI, MARTEDI, MERCOLEDI" -> ["LUNEDI", "MARTEDI", "MERCOLEDI"]
        //se non ci sono giorni disponibili crea un array vuoto
        String giorni = entity.getGiorniDisponibili();
        indirizzo.setGiorniDisponibili(
                giorni != null && !giorni.trim().isEmpty()
                        ? Arrays.asList(giorni.split(", "))
                        : new ArrayList<>()
        );
        return indirizzo;
    }


    public Indirizzo toEntity(IndirizzoDTO dto) {
        Indirizzo indirizzo = new Indirizzo();
        indirizzo.setId(dto.getId());
        indirizzo.setVia(dto.getVia());
        indirizzo.setNumeroCivico(dto.getNumeroCivico());
        indirizzo.setCitta(dto.getCitta());
        indirizzo.setProvincia(dto.getProvincia());
        indirizzo.setNomeStudio(dto.getNomeStudio());
        indirizzo.setLongitudine(dto.getLongitudine());
        indirizzo.setLatitudine(dto.getLatitudine());
        //String.join unisce tutti gli elementi separati da virgole in un unica stringa
        //io avrò giorniDispoibili = List.of("LUNNEDI", "MARTEDI")
        //e otterrò un unica stringa -->"LUNEDI, MARTEDI"
        indirizzo.setGiorniDisponibili(
                dto.getGiorniDisponibili() != null
                        ? dto.getGiorniDisponibili().stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining(", "))
                        : ""
        );
        return indirizzo;
    }

    public Indirizzo updateIndirizzo(IndirizzoDTO indirizzoDTO, Indirizzo indirizzo) {
        if (indirizzoDTO.getId() != null) {
            indirizzo.setId(indirizzoDTO.getId());
        }
        if (indirizzoDTO.getVia() != null) {
            indirizzo.setVia(indirizzoDTO.getVia());
        }
        if (indirizzoDTO.getNumeroCivico() != null) {
            indirizzo.setNumeroCivico(indirizzoDTO.getNumeroCivico());
        }
        if (indirizzoDTO.getCitta() != null) {
            indirizzo.setCitta(indirizzoDTO.getCitta());
        }
        if (indirizzoDTO.getProvincia() != null) {
            indirizzo.setProvincia(indirizzoDTO.getProvincia());
        }
        if (indirizzoDTO.getNomeStudio() != null) {
            indirizzo.setNomeStudio(indirizzoDTO.getNomeStudio());
        }
        if (indirizzoDTO.getLongitudine() != null) {
            indirizzo.setLongitudine(indirizzoDTO.getLongitudine());
        }
        if (indirizzoDTO.getLatitudine() != null) {
            indirizzo.setLatitudine(indirizzoDTO.getLatitudine());
        }
        indirizzo.setGiorniDisponibili(
                indirizzoDTO.getGiorniDisponibili().stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining(", "))
        );

        return indirizzo;

    }
}
