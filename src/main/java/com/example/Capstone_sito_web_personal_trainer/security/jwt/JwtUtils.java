package com.example.Capstone_sito_web_personal_trainer.security.jwt;

import com.example.Capstone_sito_web_personal_trainer.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
// funzionalità principali del token
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private String jwtExpiration;

    //Creazione del JWT
    //Authentication ha le info dell'utente che devo recuperare
    public String creaJwtToken(Authentication autenticazione){
        //recupero il dettaglio principal (username)
        UserDetailsImpl utentePrincipal = (UserDetailsImpl) autenticazione.getPrincipal();
        long expirationMillis = Long.parseLong(jwtExpiration); // Converte jwtExpiration in millisecondi

        Date scadenza = new Date(System.currentTimeMillis() + expirationMillis);

        //creaizone del jwt
        //con setExpiration setto la data di scadenza, il giorno di oggi in millisecondi
        //più il valore nel properties di jwt.expiration che è in millisecondi
        return Jwts.builder()
                .setSubject(utentePrincipal.getUsername())
                .claim("roles", utentePrincipal.getRuolo())
                .setIssuedAt(new Date())
                .setExpiration(scadenza)
                .signWith(recuperoChiave(), SignatureAlgorithm.HS256)
                .compact();
    }

    //recupero username dal JWT
    public String recuperoUsernameDaToken(String token){
        try {
            String username = Jwts.parserBuilder().setSigningKey(recuperoChiave()).build().parseClaimsJws(token).getBody().getSubject();
            return username;
        } catch (Exception e) {
            // Logga l'errore o gestisci la situazione in altro modo
            return null;
        }
    }

    //recupero username dal JWT
    public Date recuperoScandenzaDaToken(String token){
        Date scadenza= Jwts.parserBuilder().setSigningKey(recuperoChiave()).build().parseClaimsJws(token).getBody().getExpiration();
        return scadenza;
    }

    //validazione del TOKEN JWT
    public boolean validazioneJwtToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(recuperoChiave()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    //recupero della chiave
    public Key recuperoChiave(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }





}