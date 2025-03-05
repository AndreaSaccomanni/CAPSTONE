package com.example.Capstone_sito_web_personal_trainer.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//implemento l'interfaccia che rileva eventuali errori di autenticazione -> AuthenticationEntryPoint
//gestione errori autenticazione
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    //metodo per gestire eventuali problemi di autenticazione
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println("Errore di autenticazione: " + authException.getMessage());
        //setto il formato di ritorno verso il client
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        //setto lo status
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // setto il contenuto di ritorno verso il body del client
        Map<String, Object> infoErrori = new HashMap<>();


        infoErrori.put("stato", HttpServletResponse.SC_UNAUTHORIZED);
        infoErrori.put("errore", "Autorizzazione non valida");
        infoErrori.put("mesagio", authException.getMessage());//eccezione che viene sollevATA SE C'è QUALCHE PROBLEMA NELL'AUTENTICAZIONE
        infoErrori.put("path", request.getServletPath());




        final ObjectMapper mappaturaErrori = new ObjectMapper();
        mappaturaErrori.writeValue(response.getOutputStream(), infoErrori);
    }



}
