package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.EquipmentAuthorizationDTO;
import com.astartes.ultramar.DTO.UltramarineSelectDTO;
import com.astartes.ultramar.enumeration.SupplyEnum;
import com.astartes.ultramar.enumeration.WeightEnum;
import com.astartes.ultramar.service.EquipmentAuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ultramarines/authorizations")
public class AuthorizationEquipmentController {

    private final EquipmentAuthorizationService authService;

    public AuthorizationEquipmentController(EquipmentAuthorizationService authService) {
        this.authService = authService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentAuthorizationDTO> getByUltramarineId(@PathVariable("id") Long ultramarineId) {
        EquipmentAuthorizationDTO dto = authService.getAuthorizationsForUltramarine(ultramarineId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<EquipmentAuthorizationDTO>> getAll() {
        List<EquipmentAuthorizationDTO> allDtos = authService.findAll();
        return ResponseEntity.ok(allDtos);
    }

    @PutMapping
    public ResponseEntity<EquipmentAuthorizationDTO> update(@RequestBody EquipmentAuthorizationDTO dto) {
        Optional<EquipmentAuthorizationDTO> updated = authService.updateAuthorization(dto);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{ultramarineId}")
    public ResponseEntity<Void> deleteByUltramarineId(@PathVariable Long ultramarineId) {
        authService.deleteAllAuthorizationUltramarine(ultramarineId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unauthorized")
    public ResponseEntity<List<UltramarineSelectDTO>> getUnauthorizedUltramarines() {
        List<UltramarineSelectDTO> dtos = authService.findUltramarinesWithoutAuthorization();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/manual")
    public ResponseEntity<Void> createManualAuthorization(@RequestBody Long ultramarineId) {
        if (ultramarineId == null) return ResponseEntity.badRequest().build();

        Map<String, String> supplyMap = new LinkedHashMap<>();
        Map<String, String> weightMap = new LinkedHashMap<>();

        for (SupplyEnum supply : SupplyEnum.values()) {
            supplyMap.put(supply.name(), "unlimited");
        }

        for (WeightEnum weight : WeightEnum.values()) {
            weightMap.put(weight.name(), "unlimited");
        }

        EquipmentAuthorizationDTO dto = new EquipmentAuthorizationDTO(ultramarineId, supplyMap, weightMap);

        authService.updateAuthorization(dto);

        return ResponseEntity.noContent().build();
    }


}
