package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.MailModel;
import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import com.example.Capstone_sito_web_personal_trainer.exception.EmailDuplicateException;
import com.example.Capstone_sito_web_personal_trainer.exception.UsernameDuplicateException;
import com.example.Capstone_sito_web_personal_trainer.payload.UtenteDTO;
import com.example.Capstone_sito_web_personal_trainer.payload.mapper.UtenteMapperDTO;
import com.example.Capstone_sito_web_personal_trainer.repositories.PrenotazioneRepository;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtenteService {
    @Autowired
    UtenteRepository utenteRepository;

    @Autowired
    UtenteMapperDTO utenteMapperDTO;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PrenotazioneRepository prenotazioneRepository;

    @Autowired
    MailService mailService;

    public UtenteDTO registraUtente(UtenteDTO utenteDTO) throws InterruptedException{

        checkDuplicateKey(utenteDTO.getUsername(), utenteDTO.getEmail());

        Utente utente = utenteMapperDTO.toEntity(utenteDTO);

        //Imposto USER come ruolo di default
        if(utente.getRuolo() == null){
            utente.setRuolo(UserRole.USER);
        }

        utenteRepository.save(utente);



        // Preparazione e invio della mail
        String destinatario = utente.getEmail();
        String oggetto = "Registrazione avvenuta con successo" ;

        String contenuto = "Ciao " + utente.getNome() + ",\n\n"
                + "La registrazione è avvenuta  con successo!\n\n"
                + "Ora potrai effetturare prenotazioni per i seguenti servizi:\n\n"
                + "🎗️ CONSULENZA INIZIALE: Durante il primo incontro analizziamo insieme il tuo stato fisico attuale, i tuoi obiettivi e le tue abitudini di allenamento"  + "\n"
                + "💆‍♂️ MASSAGGIO RILASSANTE: Un massaggio della durata di un'ora che aiuta a sciogliere le tensioni accumulate e favorisce un senso di benessere generale" + "\n"
                + "🏃 MASSAGGIO DECONTRATTURANTE: Un massaggio che aiuta a eliminare l'acido lattico e migliora la circolazione per un recupero più rapido dopo l'allenamento." + "\n\n"
                + "Se hai bisogno di altre informazioni non esitare a contattarmi:\n"
                + "📞 3772082714\n"
                + "📩 alessandrosaccomanni.pt@gmail.com\n\n"
                + "Grazie!\n"
                + "A presto, cordiali saluti,\n\nDott.Alessandro";

        MailModel mailModel = new MailModel();
        mailModel.setDestinatario(destinatario);
        mailModel.setOggetto(oggetto);
        mailModel.setContenuto(contenuto);

        mailService.inviaEmail(mailModel);

        // Mapper from utente to dto
        return utenteMapperDTO.toDto(utente);
    }

    public UtenteDTO getUtenteById(Long id){
        Utente utente = utenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato!⚠️"));
        return utenteMapperDTO.toDto(utente);
    }

    public List<UtenteDTO> getAllUtenti(){
        List<Utente> utenti = utenteRepository.findAll();
        return utenti.stream().map(utenteMapperDTO::toDto).collect(Collectors.toList());
    }

    public void deleteUtente(Long id){

        Utente utente = utenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato!⚠️"));

        prenotazioneRepository.deleteAll(utente.getPrenotazioni());

        utenteRepository.deleteById(id);
    }

    public UtenteDTO updateUtente(Long id, UtenteDTO utenteDTO) {
        Utente utente = utenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato!⚠️"));
        utente = utenteMapperDTO.updateUtente(utenteDTO, utente);
        utente = utenteRepository.save(utente);
        return utenteMapperDTO.toDto(utente);
    }

    // Controllo per vedere se email o username sono già presenti nel sistema
    public void checkDuplicateKey(String username, String email) throws UsernameDuplicateException, EmailDuplicateException {
        if (utenteRepository.existsByEmail(email)) {
            throw new EmailDuplicateException("Email già presente nel sistema");
        }
        if (utenteRepository.existsByUsername(username)) {
            throw new UsernameDuplicateException("Username già presente nel sistema");
        }
    }



}
