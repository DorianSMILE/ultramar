package com.astartes.ultramar.repository;

import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    Optional<Equipment> findByEquipmentTypeAndName(EquipmentTypeEnum equipmentType, String name);
}
