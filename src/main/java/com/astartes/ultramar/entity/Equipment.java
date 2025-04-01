package com.astartes.ultramar.entity;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private EquipmentTypeEnum equipmentType;

    @ManyToMany(mappedBy = "equipments")
    private Set<Ultramarine> ultramarines = new HashSet<>();

}
