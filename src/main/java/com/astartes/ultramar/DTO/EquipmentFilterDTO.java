package com.astartes.ultramar.DTO;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.enumeration.SupplyEnum;
import com.astartes.ultramar.enumeration.WeightEnum;

public record EquipmentFilterDTO(
        WeightEnum weight,
        SupplyEnum supply,
        EquipmentTypeEnum equipmentType
) {}
