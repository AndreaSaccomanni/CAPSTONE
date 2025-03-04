package com.example.Capstone_sito_web_personal_trainer.entities;

import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ruoli")
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole name;



    // Importante!!!!!!!!!!!!!!
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
