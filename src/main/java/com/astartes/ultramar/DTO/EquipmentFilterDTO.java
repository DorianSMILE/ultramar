package com.astartes.ultramar.DTO;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.enumeration.SupplyEnum;
import com.astartes.ultramar.enumeration.WeightEnum;

import java.util.List;

public record EquipmentFilterDTO(
        List<EquipmentTypeEnum> equipmentType,
        List<SupplyEnum> supply,
        List<WeightEnum> weight
) {}
