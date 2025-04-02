package com.astartes.ultramar.controller;

import com.astartes.ultramar.enumeration.EquipmentTypeEnum;
import com.astartes.ultramar.service.EquipmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/byType")
    public Map<EquipmentTypeEnum, String> getEquipmentsMapByType() {
        return equipmentService.getAllGroupByType();
    }

}
