package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.entity.Ultramarine;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.exception.EquipmentNotFoundException;
import com.astartes.ultramar.exception.UltramarineUpdateException;
import com.astartes.ultramar.mapper.UltramarineMapper;
import com.astartes.ultramar.repository.EquipmentRepository;
import com.astartes.ultramar.repository.UltramarineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class GlobalUpdateService {

    private final UltramarineRepository ultramarineRepository;
    private final EquipmentRepository equipmentRepository;
    private final UltramarineMapper ultramarineMapper;

    public GlobalUpdateService(UltramarineRepository ultramarineRepository, EquipmentRepository equipmentRepository, UltramarineMapper ultramarineMapper) {
        this.ultramarineRepository = ultramarineRepository;
        this.equipmentRepository = equipmentRepository;
        this.ultramarineMapper = ultramarineMapper;
    }

    @Transactional
    public UltramarineDTO updateGlobal(UltramarineDTO dto) {
        Ultramarine ultramarine = ultramarineRepository.findById(dto.id())
                .orElseThrow(() -> new UltramarineUpdateException(dto.id()));

        if(dto.name() != null && dto.grade() != null) {
            ultramarine.setName(dto.name());
            ultramarine.setGrade(dto.grade());
        }

        Map<EquipmentTypeEnum, Equipment> newEquipments = new HashMap<>();
        if (dto.equipments() != null) {
            for (EquipmentDTO equipmentDTO : dto.equipments()) {
                if (equipmentDTO.name() == null || equipmentDTO.name().trim().isEmpty()) {
                    continue;
                }
                EquipmentTypeEnum typeEnum = equipmentDTO.equipmentType();
                Equipment equipment = equipmentRepository.findByEquipmentTypeAndName(typeEnum, equipmentDTO.name())
                        .orElseThrow(() -> new EquipmentNotFoundException(
                                equipmentDTO.equipmentType() + " - " + equipmentDTO.name()));
                newEquipments.put(equipment.getEquipmentType(), equipment);
            }
            ultramarine.setEquipments(newEquipments);
        }
        Ultramarine saved = ultramarineRepository.saveAndFlush(ultramarine);
        return ultramarineMapper.toDTO(saved);
    }

}
