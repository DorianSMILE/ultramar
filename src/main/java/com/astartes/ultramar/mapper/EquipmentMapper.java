package com.astartes.ultramar.mapper;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import org.springframework.stereotype.Component;

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
        equipment.setEquipmentType(EquipmentTypeEnum.valueOf(String.valueOf(dto.equipmentType())));
        return equipment;
    }

    public EquipmentDTO toDto(Equipment entity) {
        if (entity == null) {
            return null;
        }
        return new EquipmentDTO(entity.getId(), entity.getName(), entity.getEquipmentType());
    }
}