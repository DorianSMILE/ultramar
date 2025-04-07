package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.UltramarineDTO;
import com.astartes.ultramar.service.GlobalUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ultramarine")
public class GlobalUpdateController {

    private final GlobalUpdateService globalUpdateService;

    public GlobalUpdateController(GlobalUpdateService globalUpdateService) {
        this.globalUpdateService = globalUpdateService;
    }

    /**
     * Met à jours l'ultramarine et ces équipements
     *
     * @param ultramarineDTO
     * @return
     */
    @PutMapping("/global")
    public ResponseEntity<UltramarineDTO> updateGlobal(@Valid @RequestBody UltramarineDTO ultramarineDTO) {
        return ResponseEntity.ok(globalUpdateService.updateGlobal(ultramarineDTO));
    }
}