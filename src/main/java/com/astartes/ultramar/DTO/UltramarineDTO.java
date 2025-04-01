package com.astartes.ultramar.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record UltramarineDTO(
    Integer id,
    @NotBlank
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "Name should contain only letters, spaces, or hyphens")
    String name,
    @NotBlank
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "Grade should contain only letters, spaces, or hyphens")
    String grade,
    List<EquipmentDTO> equipments
) {
    public UltramarineDTO() {
        this(null, "", "", List.of());
    }
}
