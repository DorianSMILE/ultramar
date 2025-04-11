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

    @Column(name = "um_id", nullable = false)
    private int ultramarineId;

    @Column(name = "categ_auth", nullable = false)
    private String category;

    @Column(name = "nb_auth")
    private Integer nbAuthorized;

}
