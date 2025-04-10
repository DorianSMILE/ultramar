package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.DTO.EquipmentFilterDTO;
import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.mapper.EquipmentMapper;
import com.astartes.ultramar.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    public EquipmentService(EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentMapper = equipmentMapper;
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

    public List<EquipmentDTO> getEquipmentsByType(EquipmentFilterDTO equipmentFilter) {
        if (equipmentFilter == null ||
                (equipmentFilter.equipmentType() == null && equipmentFilter.supply() == null && equipmentFilter.weight() == null)) {
            return equipmentMapper.toDto(equipmentRepository.findAll());
        }

        return equipmentMapper.toDto(equipmentRepository.findEquipmentsByFilters(
                equipmentFilter.equipmentType(),
                equipmentFilter.supply(),
                equipmentFilter.weight()));
    }

}
