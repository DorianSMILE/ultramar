package com.astartes.ultramar.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateDTO(
        Integer id,
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotNull
        Long roleId
) {}