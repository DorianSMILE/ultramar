package com.astartes.ultramar.DTO;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.enumeration.SupplyEnum;
import com.astartes.ultramar.enumeration.WeightEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EquipmentDTO(
        Integer id,
        @NotBlank(message = "Le nom est obligatoire")
        String name,
        @NotNull(message = "Le type d'équipement est obligatoire")
        EquipmentTypeEnum equipmentType,
        SupplyEnum supply,
        WeightEnum weight
) {
    public EquipmentDTO() {
        this(null, "", null, null, null);
    }
}