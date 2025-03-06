package com.example.Capstone_sito_web_personal_trainer.repositories;

import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {


    // Trova tutte le prenotazioni di un utente
    List<Prenotazione> findByUtente(Utente utente);

    // Trova tutte le prenotazioni in un intervallo di tempo
    List<Prenotazione> findAllByDataOraBetween( LocalDateTime start, LocalDateTime end);

    boolean existsByDataOra(LocalDateTime dataOra);



}
