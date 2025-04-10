package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.DTO.EquipmentFilterDTO;
import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.exception.EquipmentNotFoundException;
import com.astartes.ultramar.mapper.EquipmentMapper;
import com.astartes.ultramar.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        List<Equipment> equipments = equipmentRepository.findAll();
        return Optional.of(equipments)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .collect(Collectors.groupingBy(
                                Equipment::getEquipmentType,
                                Collectors.mapping(Equipment::getName, Collectors.toList())
                        )))
                .orElseThrow(() -> new EquipmentNotFoundException("Aucun équipement disponible"));
    }


    public Map<EquipmentTypeEnum, String> getUltramarineEquipments(UltramarineDTO ultramarineDTO) {
        List<EquipmentDTO> equipments = Optional.ofNullable(ultramarineDTO)
                .map(UltramarineDTO::equipments)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new EquipmentNotFoundException("Aucun équipement ultramarine trouvé pour les données fournies"));

        return equipments.stream()
                .collect(Collectors.toMap(
                        EquipmentDTO::equipmentType,
                        EquipmentDTO::name,
                        (existing, replacement) -> existing
                ));
    }

    public List<EquipmentDTO> getEquipmentsByType(EquipmentFilterDTO equipmentFilter) {
        List<Equipment> equipments = Optional.ofNullable(equipmentFilter)
                .filter(filter -> filter.equipmentType() != null || filter.supply() != null || filter.weight() != null)
                .map(filter -> equipmentRepository.findEquipmentsByFilters(
                        filter.equipmentType(), filter.supply(), filter.weight()))
                .orElseGet(equipmentRepository::findAll);

        return Optional.of(equipments)
                .filter(list -> !list.isEmpty())
                .map(equipmentMapper::toDto)
                .orElseThrow(() -> new EquipmentNotFoundException("Aucun équipement trouvé avec les filtres donnés"));
    }

}
