package com.astartes.ultramar.repository;

import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.enumeration.SupplyEnum;
import com.astartes.ultramar.enumeration.WeightEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    Optional<Equipment> findByEquipmentTypeAndName(EquipmentTypeEnum equipmentType, String name);

    @Query("SELECT e FROM Equipment e " +
            "WHERE (:equipmentType IS NULL OR e.equipmentType = :equipmentType) " +
            "AND (:supply IS NULL OR e.supply = :supply) " +
            "AND (:weight IS NULL OR e.weight = :weight)")
    List<Equipment> findEquipmentsByFilters(@Param("equipmentType") EquipmentTypeEnum equipmentType,
                                            @Param("supply") SupplyEnum supply,
                                            @Param("weight") WeightEnum weight);

}
