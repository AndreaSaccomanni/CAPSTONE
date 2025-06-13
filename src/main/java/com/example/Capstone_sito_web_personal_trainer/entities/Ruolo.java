package com.example.Capstone_sito_web_personal_trainer.entities;

import com.example.Capstone_sito_web_personal_trainer.enumeration.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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


//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name);
//    } //due oggetti Ruolo con lo stesso id e con lo stesso nome avranno lo stesso hashcode

}
