package com.astartes.ultramar.DTO;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;

import java.util.Map;

public record EquipmentResponseDTO(Map<EquipmentTypeEnum, String> equipments) { }