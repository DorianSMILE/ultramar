package com.astartes.ultramar.entity;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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

    @ManyToMany
    @JoinTable(
            name = "ultramarine_equipment",
            joinColumns = @JoinColumn(name = "ultramarine_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    @MapKey(name = "equipmentType")
    private Map<EquipmentTypeEnum, Equipment> equipments = new HashMap<>();

    public Ultramarine(String name, String grade, Map<EquipmentTypeEnum, Equipment> equipements) {
        this.name = name;
        this.grade = grade;
        this.equipments = equipements;
    }
}
