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
            "WHERE (:equipmentTypes IS NULL OR e.equipmentType IN :equipmentTypes) " +
            "AND (:supplies IS NULL OR e.supply IN :supplies) " +
            "AND (:weights IS NULL OR e.weight IN :weights)")
    List<Equipment> findEquipmentsByFilters(@Param("equipmentTypes") List<EquipmentTypeEnum> equipmentTypes,
                                            @Param("supplies") List<SupplyEnum> supplies,
                                            @Param("weights") List<WeightEnum> weights);

}
