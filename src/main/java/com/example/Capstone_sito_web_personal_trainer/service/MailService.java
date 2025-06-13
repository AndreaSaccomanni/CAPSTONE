package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.MailModel;
import com.example.Capstone_sito_web_personal_trainer.payload.FormInformazioniDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // Metodo per inviare una mail semplice
    public String inviaEmail(MailModel mailModel) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mailModel.getDestinatario());

        message.setSubject(mailModel.getOggetto());

        message.setText(mailModel.getContenuto());


        try {
            mailSender.send(message);

            logger.info("Mail inviata correttamente a: {}", mailModel.getDestinatario());
            return "✅ Mail inviata correttamente";

        } catch (MailException e) {

            logger.error("Errore nell'invio della mail: {}", e.getMessage());
            return "Errore nell'invio della mail";

        }
    }


    public String inviaForm(FormInformazioniDTO formInformazioniDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("alessandrosaccomanni.pt@gmail.com");
        message.setFrom("alessandrosaccomanni.pt@gmail.com");
        message.setSubject("Richiesta informazioni");
        message.setReplyTo(formInformazioniDTO.getEmail()); // quando si risponde all'email, la risposta arriva all'utente anche se il mittente è: ("alessandrosaccomanni.pt@gmail.com");
        message.setText(
                "Nome: " + formInformazioniDTO.getNome() + "\n" +
                        "Cognome: " + formInformazioniDTO.getCognome() + "\n" +
                        "Messaggio: " + formInformazioniDTO.getMessaggio() + "\n"
        );
        try {
            mailSender.send(message);
            logger.info("Mail inviata da {} a {}", formInformazioniDTO.getEmail(), "alessandrosaccomanni.pt@gmail.com");
            return "✅ Email inviata correttamente!";
        } catch (MailException e) {
            logger.error("Errore nell'invio dell'email: {}", e.getMessage());
            return "❌ Errore durante l'invio dell'email.";
        }
    }
}