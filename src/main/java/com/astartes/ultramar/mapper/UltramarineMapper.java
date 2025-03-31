package com.astartes.ultramar.mapper;

import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Ultramarine;
import org.springframework.stereotype.Component;

@Component
public class UltramarineMapper {
    public Ultramarine toEntity(UltramarineDTO dto) {
        if (dto == null) return null;
        return new Ultramarine(dto.name(), dto.grade());
    }

    public UltramarineDTO toDTO(Ultramarine entity) {
        if (entity == null) return null;
        return new UltramarineDTO(entity.getId(), entity.getName(), entity.getGrade());
    }
}
