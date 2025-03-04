package com.example.Capstone_sito_web_personal_trainer.repositories;

import com.example.Capstone_sito_web_personal_trainer.entities.Prenotazione;
import com.example.Capstone_sito_web_personal_trainer.entities.Servizio;
import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {


    // Trova tutte le prenotazioni di un utente
    List<Prenotazione> findByUtente(Utente utente);

    // Trova tutte le prenotazioni in un intervallo di tempo
    List<Prenotazione> findByDataOraBetween( LocalDateTime start, LocalDateTime end);

    //Per verificare se un determinato servizio è già prenotato in un momento specifico
    @Query("SELECT p FROM Prenotazione p WHERE p.servizio = :servizio AND p.dataOra = :dataOra")
    List<Prenotazione> findByServizioAndDataOra(@Param("servizio") Servizio servizio, @Param("dataOra") LocalDateTime dataOra);

    boolean existsByDataOra(LocalDateTime dataOra);
}
