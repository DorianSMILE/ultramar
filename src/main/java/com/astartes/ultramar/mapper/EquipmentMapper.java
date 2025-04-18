package com.astartes.ultramar.mapper;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.entity.Equipment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EquipmentMapper {

    public Equipment toEntity(EquipmentDTO dto) {
        if (dto == null) {
            return null;
        }
        Equipment equipment = new Equipment();
        if (dto.id() != null) {
            equipment.setId(dto.id());
        }
        equipment.setName(dto.name());
        equipment.setEquipmentType(dto.equipmentType());
        equipment.setSupply(dto.supply());
        equipment.setWeight(dto.weight());
        return equipment;
    }

    public EquipmentDTO toDto(Equipment entity) {
        if (entity == null) {
            return null;
        }
        return new EquipmentDTO(
                entity.getId(),
                entity.getName(),
                entity.getEquipmentType(),
                entity.getSupply(),
                entity.getWeight()
        );
    }

    public List<EquipmentDTO> toDto(List<Equipment> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
