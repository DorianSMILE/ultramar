package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.entity.Ultramarine;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.exception.EquipmentNotFoundException;
import com.astartes.ultramar.exception.UltramarineUpdateException;
import com.astartes.ultramar.repository.EquipmentRepository;
import com.astartes.ultramar.repository.UltramarineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UltramarineRepository ultramarineRepository;

    public EquipmentService(EquipmentRepository equipmentRepository, UltramarineRepository ultramarineRepository) {
        this.equipmentRepository = equipmentRepository;
        this.ultramarineRepository = ultramarineRepository;
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
    public void updateUltramarineEquipments(UltramarineDTO ultramarineDTO) {
        Ultramarine ultramarine = ultramarineRepository.findById(ultramarineDTO.id())
                .orElseThrow(() -> new UltramarineUpdateException(ultramarineDTO.id()));

        ultramarine.getEquipments().clear();
        if (ultramarineDTO.equipments() != null) {
            for (EquipmentDTO equipmentDTO : ultramarineDTO.equipments()) {
                if (equipmentDTO.name() == null || equipmentDTO.name().trim().isEmpty()) {
                    continue;
                }
                Equipment equipment = equipmentRepository
                        .findByEquipmentTypeAndName(equipmentDTO.equipmentType(), equipmentDTO.name())
                        .orElseThrow(() -> new EquipmentNotFoundException(equipmentDTO.equipmentType() + " - " + equipmentDTO.name()));
                ultramarine.getEquipments().put(equipment.getEquipmentType(), equipment);
            }
        }

        ultramarineRepository.save(ultramarine);
    }
}
