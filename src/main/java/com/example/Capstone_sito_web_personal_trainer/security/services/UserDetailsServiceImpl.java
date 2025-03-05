package com.example.Capstone_sito_web_personal_trainer.security.services;

import com.example.Capstone_sito_web_personal_trainer.entities.Utente;
import com.example.Capstone_sito_web_personal_trainer.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByUsername(username)

                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con username: " + username));

        return UserDetailsImpl.costruisciOggetto(utente);
    }
}
