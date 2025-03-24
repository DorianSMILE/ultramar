package com.astartes.ultramar.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoleDTO(
        @NotNull
        Long roleId,
        @NotBlank
        String role
) {}