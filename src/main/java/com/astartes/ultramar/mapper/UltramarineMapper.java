package com.astartes.ultramar.mapper;

import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Ultramarine;

public class UltramarineMapper {
    public static Ultramarine toEntity(UltramarineDTO dto) {
        if (dto == null) return null;
        return new Ultramarine(dto.name(), dto.grade());
    }

    public static UltramarineDTO toDTO(Ultramarine entity) {
        if (entity == null) return null;
        return new UltramarineDTO(entity.getId(), entity.getName(), entity.getGrade());
    }
}
