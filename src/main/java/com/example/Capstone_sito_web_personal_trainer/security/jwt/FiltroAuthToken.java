package com.example.Capstone_sito_web_personal_trainer.security.jwt;

import com.example.Capstone_sito_web_personal_trainer.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroAuthToken extends OncePerRequestFilter {

    @Autowired
    private final JwtUtils jwtUtils;

    private final UserDetailsServiceImpl userDetailsService;


    public FiltroAuthToken(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Se la richiesta è per la registrazione, bypassa il filtro
        if (isRegistrationRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = recuperaJwtDallaRequest(request);

        if (jwt != null && jwtUtils.validazioneJwtToken(jwt)) {
            String username = jwtUtils.recuperoUsernameDaToken(jwt);
            UserDetails dettagliUtente = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken autenticazione = new UsernamePasswordAuthenticationToken(
                    dettagliUtente, null, dettagliUtente.getAuthorities());
            autenticazione.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(autenticazione);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRegistrationRequest(HttpServletRequest request) {
        return "/utenti/registrazione".equals(request.getRequestURI());
    }

    private String recuperaJwtDallaRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
