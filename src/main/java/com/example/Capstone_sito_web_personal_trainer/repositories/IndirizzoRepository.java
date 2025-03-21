package com.example.Capstone_sito_web_personal_trainer.repositories;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndirizzoRepository extends JpaRepository< Indirizzo, Long> {


    Optional<Indirizzo> findByNomeStudio(String nomeStudio);
}
