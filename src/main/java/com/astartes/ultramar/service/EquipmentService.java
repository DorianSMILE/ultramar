package com.astartes.ultramar.service;

import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public Map<EquipmentTypeEnum, String> getAllGroupByType() {
        return equipmentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Equipment::getEquipmentType,
                        Equipment::getName,
                        (existing, replacement) -> existing
                ));
    }
}
