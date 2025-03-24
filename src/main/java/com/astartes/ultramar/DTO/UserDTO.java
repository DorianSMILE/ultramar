package com.astartes.ultramar.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserDTO(
        @NotBlank
        String username,
        @NotBlank
        String password
) {}