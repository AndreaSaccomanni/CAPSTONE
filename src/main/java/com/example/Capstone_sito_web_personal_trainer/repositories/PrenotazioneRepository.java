package com.example.Capstone_sito_web_personal_trainer.repositories;

import com.example.Capstone_sito_web_personal_trainer.entities.Indirizzo;
import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {


    // Trova tutte le prenotazioni di un utente
    List<Prenotazione> findByUtente(Utente utente);

    // Trova tutte le prenotazioni in un intervallo di tempo
    List<Prenotazione> findByDataOraBetween( LocalDateTime start, LocalDateTime end);

    boolean existsByDataOra(LocalDateTime dataOra);

    //seleziona solamente le prenotazioni future senza tenere conto di quelle passate
    List<Prenotazione> findByDataOraAfter(LocalDateTime now);

    List<Prenotazione> findByDataOraAfterAndUtente(LocalDateTime now, Utente utente);

    List<Prenotazione> findByIndirizzo(Indirizzo indirizzo);



}
