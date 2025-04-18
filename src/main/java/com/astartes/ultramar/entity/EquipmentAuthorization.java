package com.astartes.ultramar.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "equipment_authorizations")
public class EquipmentAuthorization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ultramarine_id", nullable = false)
    private Ultramarine ultramarine;

    @Column(name = "categ_auth", nullable = false)
    private String category;

    @Column(name = "nb_auth")
    private Integer nbAuthorized;

}
