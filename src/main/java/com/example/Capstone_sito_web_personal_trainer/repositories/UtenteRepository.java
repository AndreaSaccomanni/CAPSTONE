package com.example.Capstone_sito_web_personal_trainer.repositories;

import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByUsername(String username);

    Optional<Utente> findOneByEmail(String email);

    List<Utente> findAll();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
