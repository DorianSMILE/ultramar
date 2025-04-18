package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.EquipmentAuthorizationDTO;
import com.astartes.ultramar.service.EquipmentAuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (authService.findById(id).isPresent()) {
            authService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
