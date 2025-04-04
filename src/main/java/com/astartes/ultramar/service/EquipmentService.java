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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UltramarineRepository ultramarineRepository;
    private final UltramarineMapper ultramarineMapper;

    public EquipmentService(EquipmentRepository equipmentRepository, UltramarineRepository ultramarineRepository, UltramarineMapper ultramarineMapper) {
        this.equipmentRepository = equipmentRepository;
        this.ultramarineRepository = ultramarineRepository;
        this.ultramarineMapper = ultramarineMapper;
    }

    public Map<EquipmentTypeEnum, List<String>> getAvailableEquipmentsGroupedByType() {
        return equipmentRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Equipment::getEquipmentType,
                        Collectors.mapping(Equipment::getName, Collectors.toList())
                ));
    }

    public Map<EquipmentTypeEnum, String> getUltramarineEquipments(UltramarineDTO ultramarineDTO) {
        return ultramarineDTO.equipments()
                .stream()
                .collect(Collectors.toMap(
                        EquipmentDTO::equipmentType,
                        EquipmentDTO::name,
                        (existing, replacement) -> existing
                ));
    }

    @Transactional
    public UltramarineDTO updateUltramarineEquipments(UltramarineDTO dto) {
        Ultramarine ultramarine = ultramarineRepository.findById(dto.id())
                .orElseThrow(() -> new UltramarineUpdateException(dto.id()));

        ultramarine.getEquipments().clear();

        Map<EquipmentTypeEnum, Equipment> newEquipments = new HashMap<>();
        if (dto.equipments() != null) {
            for (EquipmentDTO equipmentDTO : dto.equipments()) {
                Equipment equipment = equipmentRepository.findByEquipmentTypeAndName(
                                equipmentDTO.equipmentType(),
                                equipmentDTO.name())
                        .orElseThrow(() -> new EquipmentNotFoundException(
                                equipmentDTO.equipmentType() + " - " + equipmentDTO.name()));
                newEquipments.put(equipment.getEquipmentType(), equipment);
            }
        }
        ultramarine.setEquipments(newEquipments);
        ultramarineRepository.saveAndFlush(ultramarine);
        return ultramarineMapper.toDTO(ultramarine);
    }
}
