package com.astartes.ultramar.DTO;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EquipmentDTO(
        Integer id,
        @NotBlank(message = "Le nom est obligatoire")
        String name,
        @NotNull(message = "Le type d'équipement est obligatoire")
        EquipmentTypeEnum equipmentType
) {
    public EquipmentDTO() {
        this(null, "", null);
    }
}