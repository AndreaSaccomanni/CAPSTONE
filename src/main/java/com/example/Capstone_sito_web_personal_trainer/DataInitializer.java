package com.example.Capstone_sito_web_personal_trainer;


import com.example.Capstone_sito_web_personal_trainer.entities.Ruolo;
import com.example.Capstone_sito_web_personal_trainer.enumeration.TipoConsulenza;
import com.example.Capstone_sito_web_personal_trainer.enumeration.TipoMassaggio;
import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import com.example.Capstone_sito_web_personal_trainer.payload.ConsulenzaDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.MassaggioDTO;
import com.example.Capstone_sito_web_personal_trainer.repositories.ConsulenzaRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.MassaggioRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.RuoloRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import com.example.Capstone_sito_web_personal_trainer.service.ConsulenzaService;
import com.example.Capstone_sito_web_personal_trainer.service.MassaggioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RuoloRepository ruoloRepository;
    @Autowired
    private MassaggioRepository massaggioRepository;
    @Autowired
    private ConsulenzaRepository consulenzaRepository;
    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private MassaggioService massaggioService;

    @Autowired
    private ConsulenzaService consulenzaService;

    @Autowired
    PasswordEncoder passwordEncoder;

// ------------- INSERIMENTO AUTOMATICO DI RUOLI, SERVIZI E ADMIN ----------------
    @Override
    public void run(String... args) throws Exception {
        if (ruoloRepository.count() == 0) {

            Ruolo adminRole = new Ruolo();
            adminRole.setName(UserRole.ADMIN);
            ruoloRepository.save(adminRole);

            Ruolo userRole = new Ruolo();
            userRole.setName(UserRole.USER);
            ruoloRepository.save(userRole);

            Ruolo personalRole = new Ruolo();
            personalRole.setName(UserRole.PERSONAL_TRAINER);
            ruoloRepository.save(personalRole);
        }


        if (massaggioRepository.count() == 0) {
            MassaggioDTO massaggio1DTO = new MassaggioDTO();
            massaggio1DTO.setTipoMassaggio(TipoMassaggio.RILASSANTE);
            massaggioService.creaMassaggio(massaggio1DTO);

            MassaggioDTO massaggio2DTO = new MassaggioDTO();
            massaggio2DTO.setTipoMassaggio(TipoMassaggio.DECONTRATTURANTE_SPORTIVO);
            massaggioService.creaMassaggio(massaggio2DTO);
        }


        if(consulenzaRepository.count() == 0) {
            ConsulenzaDTO consulenza1DTO = new ConsulenzaDTO();
            consulenza1DTO.setTipoConsulenza(TipoConsulenza.VALUTAZIONE_INIZIALE);
            consulenzaService.creaConsulenza(consulenza1DTO);

            ConsulenzaDTO consulenza2DTO = new ConsulenzaDTO();
            consulenza2DTO.setTipoConsulenza(TipoConsulenza.MIGLIORAMENTO_SCHEDA);
            consulenzaService.creaConsulenza(consulenza2DTO);
        }



    }
}
