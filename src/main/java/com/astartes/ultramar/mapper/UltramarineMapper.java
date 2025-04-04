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

    public UltramarineDTO toDTO(Ultramarine entity) {
        if (entity == null) return null;

        List<EquipmentDTO> equipmentDTOList = entity.getEquipments() != null
                ? entity.getEquipments().values().stream()
                .map(equipmentMapper::toDto)
                .toList()
                : List.of();

        return new UltramarineDTO(entity.getId(), entity.getName(), entity.getGrade(), equipmentDTOList);
    }
}
