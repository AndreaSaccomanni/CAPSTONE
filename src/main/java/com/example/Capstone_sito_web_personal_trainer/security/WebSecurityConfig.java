package com.example.Capstone_sito_web_personal_trainer.security;

import com.example.Capstone_sito_web_personal_trainer.security.jwt.AuthEntryPoint;
import com.example.Capstone_sito_web_personal_trainer.security.jwt.FiltroAuthToken;
import com.example.Capstone_sito_web_personal_trainer.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl detailsImpl;
    private final FiltroAuthToken filtroAuthToken;
    private final AuthEntryPoint gestoreNOAuthorization;

    public WebSecurityConfig(UserDetailsServiceImpl detailsImpl, FiltroAuthToken filtroAuthToken, AuthEntryPoint gestoreNOAuthorization) {
        this.detailsImpl = detailsImpl;
        this.filtroAuthToken = filtroAuthToken;
        this.gestoreNOAuthorization = gestoreNOAuthorization;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(detailsImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(gestoreNOAuthorization))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/utenti/registrazione").permitAll()
                        .requestMatchers("/utenti/login").permitAll()
                        .requestMatchers("/mail/sendMail").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Permessi per USER
                        .requestMatchers("/prenotazioni/new").hasAuthority("USER")
                        .requestMatchers("/prenotazioni/update/**").hasAuthority("USER")
                        .requestMatchers("/prenotazioni/delete/**").hasAuthority("USER")

                        // Permessi per PERSONAL_TRAINER
                        .requestMatchers("/prenotazioni/**").hasAuthority("PERSONAL_TRAINER")

                        // Permessi per ADMIN (può fare tutto)
                        .requestMatchers("/prenotazioni/**").hasAuthority("ADMIN")
                        .requestMatchers("/utenti/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(filtroAuthToken, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
