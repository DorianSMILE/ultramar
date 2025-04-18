package com.astartes.ultramar.entity;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "ultramarine")
@AllArgsConstructor
@NoArgsConstructor
public class Ultramarine {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

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

    @OneToMany(mappedBy = "ultramarine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipmentAuthorization> equipmentAuthorizations = new ArrayList<>();


    public Ultramarine(String name, String grade, Map<EquipmentTypeEnum, Equipment> equipements) {
        this.name = name;
        this.grade = grade;
        this.equipments = equipements;
    }
}
