package com.example.Capstone_sito_web_personal_trainer.service;

import com.example.Capstone_sito_web_personal_trainer.entities.MailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // Metodo per inviare una mail semplice
    public String inviaEmail(MailModel mailModel) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("emailsenderale@libero.it");

        message.setTo(mailModel.getDestinatario());

        message.setSubject(mailModel.getOggetto());

        message.setText(mailModel.getContenuto());

        try {
            mailSender.send(message);

            logger.info("Mail inviata correttamente a: {}", mailModel.getDestinatario());
            return "✅ Mail inviata correttamente";

        } catch (MailException e) {

            logger.error("Errore nell'invio della mail: {}", e.getMessage());
            return " ❌ Errore nell'invio della mail";

        }
    }
}
