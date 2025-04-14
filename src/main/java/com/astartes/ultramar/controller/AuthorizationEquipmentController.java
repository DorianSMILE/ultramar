package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.UltramarineAuthorizationDTO;
import com.astartes.ultramar.service.EquipmentAuthorizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ultramarines/authorizations")
public class AuthorizationEquipmentController {

    private final EquipmentAuthorizationService authService;

    public AuthorizationEquipmentController(EquipmentAuthorizationService authService) {
        this.authService = authService;
    }

    @GetMapping("/{id}")
    public UltramarineAuthorizationDTO getAuthorizations(@PathVariable("id") int ultramarineId) {
        return authService.getAuthorizationsForUltramarine(ultramarineId);
    }

}
