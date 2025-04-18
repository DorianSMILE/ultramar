package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.entity.Equipment;
import com.astartes.ultramar.entity.Ultramarine;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.exception.EquipmentNotAuthorizedException;
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
    private final EquipmentAuthorizationService equipmentAuthorizationService;

    public GlobalUpdateService(UltramarineRepository ultramarineRepository, EquipmentRepository equipmentRepository, UltramarineMapper ultramarineMapper, EquipmentAuthorizationService equipmentAuthorizationService) {
        this.ultramarineRepository = ultramarineRepository;
        this.equipmentRepository = equipmentRepository;
        this.ultramarineMapper = ultramarineMapper;
        this.equipmentAuthorizationService = equipmentAuthorizationService;
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
        EquipmentVerificationContext verificationContext = new EquipmentVerificationContext(ultramarine.getId());

        if (dto.equipments() != null) {
            for (EquipmentDTO equipmentDTO : dto.equipments()) {
                if (equipmentDTO.name() == null || equipmentDTO.name().trim().isEmpty()) {
                    continue;
                }
                EquipmentTypeEnum typeEnum = equipmentDTO.equipmentType();
                Equipment equipment = equipmentRepository.findByEquipmentTypeAndName(typeEnum, equipmentDTO.name())
                        .orElseThrow(() -> new EquipmentNotFoundException(
                                equipmentDTO.equipmentType() + " - " + equipmentDTO.name()));

                if (equipment.getSupply() != null) {
                    String supplyCategory = equipment.getSupply().name();
                    verificationContext.checkSupply(supplyCategory);
                }

                if (equipment.getWeight() != null) {
                    String weightCategory = equipment.getWeight().name();
                    verificationContext.checkWeight(weightCategory);
                }

                newEquipments.put(equipment.getEquipmentType(), equipment);
            }
            ultramarine.setEquipments(newEquipments);
        }
        Ultramarine saved = ultramarineRepository.saveAndFlush(ultramarine);
        return ultramarineMapper.toDTO(saved);
    }

    private class EquipmentVerificationContext {
        private final Map<String, Integer> supplyCountMap = new HashMap<>();
        private final Map<String, Integer> weightCountMap = new HashMap<>();
        private final Long ultramarineId;

        EquipmentVerificationContext(Long ultramarineId) {
            this.ultramarineId = ultramarineId;
        }

        void checkSupply(String supplyCategory) {
            int currentCount = supplyCountMap.getOrDefault(supplyCategory, 0);
            if (equipmentAuthorizationService.isAuthorized(ultramarineId, supplyCategory, currentCount + 1)) {
                throw new EquipmentNotAuthorizedException("Ultramarine " + ultramarineId
                        + " n'est pas autorisé à porter un autre équipement de type supply " + supplyCategory);
            }
            supplyCountMap.put(supplyCategory, currentCount + 1);
        }

        void checkWeight(String weightCategory) {
            int currentCount = weightCountMap.getOrDefault(weightCategory, 0);
            if (equipmentAuthorizationService.isAuthorized(ultramarineId, weightCategory, currentCount + 1)) {
                throw new EquipmentNotAuthorizedException("Ultramarine " + ultramarineId
                        + " n'est pas autorisé à porter un autre équipement de type weight " + weightCategory);
            }
            weightCountMap.put(weightCategory, currentCount + 1);
        }
    }


}
