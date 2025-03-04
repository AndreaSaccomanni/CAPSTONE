package com.example.Capstone_sito_web_personal_trainer.repositories;
import com.example.Capstone_sito_web_personal_trainer.entities.Ruolo;
import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuoloRepository extends JpaRepository<Ruolo, Long> {
    Ruolo findByName(UserRole name);
}
