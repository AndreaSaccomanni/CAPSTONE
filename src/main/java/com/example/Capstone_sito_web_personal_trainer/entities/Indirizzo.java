package com.example.Capstone_sito_web_personal_trainer.entities;

//import com.example.Capstone_sito_web_personal_trainer.enumeration.GiorniDisponibili;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "indirizzi")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Indirizzo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String via;
    private String numeroCivico;
    private String citta;
    private String provincia;
    private Double latitudine;
    private Double longitudine;
    private String nomeStudio;


    private String giorniDisponibili;


}
