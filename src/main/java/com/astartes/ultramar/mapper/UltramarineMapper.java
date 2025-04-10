package com.astartes.ultramar.mapper;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.entity.Ultramarine;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UltramarineMapper {

    private final EquipmentMapper equipmentMapper;

    public UltramarineMapper(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    public Ultramarine toEntity(UltramarineDTO dto) {
        if (dto == null) return null;

        Map<EquipmentTypeEnum, Equipment> equipmentMap =
                dto.equipments() != null
                        ? dto.equipments().stream().collect(
                        Collectors.toMap(
                                EquipmentDTO::equipmentType,
                                equipmentMapper::toEntity
                        )
                )
                        : new HashMap<>();

        return new Ultramarine(dto.name(), dto.grade(), equipmentMap);
    }

    public UltramarineDTO toDTO(Ultramarine ultramarine) {
        Map<EquipmentTypeEnum, Equipment> equipments = new HashMap<>(ultramarine.getEquipments());
        List<EquipmentDTO> equipmentDTOs = equipments.values().stream()
                .map(equipment -> new EquipmentDTO(equipment.getId(), equipment.getName(), equipment.getEquipmentType(), equipment.getSupply(), equipment.getWeight()))
                .collect(Collectors.toList());
        return new UltramarineDTO(ultramarine.getId(), ultramarine.getName(), ultramarine.getGrade(), equipmentDTOs);
    }

}
