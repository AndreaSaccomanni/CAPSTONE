package com.example.Capstone_sito_web_personal_trainer.repositories;

import com.example.Capstone_sito_web_personal_trainer.entities.Massaggio;
import com.example.Capstone_sito_web_personal_trainer.enumeration.TipoMassaggio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MassaggioRepository extends JpaRepository<Massaggio, Long> {
    boolean existsByTipoMassaggio(TipoMassaggio tipoMassaggio);
}
