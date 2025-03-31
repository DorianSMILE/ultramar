package com.astartes.ultramar.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangePasswordDTO(
        @NotNull
        UUID uuid,
        @NotBlank
        String password
) {}
