package com.astartes.ultramar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ultramarine")
@AllArgsConstructor
@NoArgsConstructor
public class Ultramarine {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    String grade;

    public Ultramarine(String name, String grade) {
        this.name = name;
        this.grade = grade;
    }
}
