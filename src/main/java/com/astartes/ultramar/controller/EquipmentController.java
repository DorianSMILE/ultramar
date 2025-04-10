package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.EquipmentDTO;
import com.astartes.ultramar.DTO.EquipmentFilterDTO;
import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.service.EquipmentService;
import com.astartes.ultramar.service.UltramarineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final UltramarineService ultramarineService;

    public EquipmentController(EquipmentService equipmentService, UltramarineService ultramarineService) {
        this.equipmentService = equipmentService;
        this.ultramarineService = ultramarineService;
    }

    @GetMapping("/available/byType")
    public Map<EquipmentTypeEnum, List<String>> getAvailableEquipmentsGroupedByType() {
        return equipmentService.getAvailableEquipmentsGroupedByType();
    }

    @GetMapping("/ultramarine/{id}")
    public Map<EquipmentTypeEnum, String> getUltramarineEquipments(@PathVariable("id") int id) {
        UltramarineDTO ultramarineDTO = ultramarineService.getById(id);
        return equipmentService.getUltramarineEquipments(ultramarineDTO);
    }

    @PostMapping("/filtre")
    public List<EquipmentDTO> getUltramarineEquipmentsFiltre(@RequestBody(required = false) EquipmentFilterDTO equipmentFilter) {
        return equipmentService.getEquipmentsByType(equipmentFilter);
    }

}
