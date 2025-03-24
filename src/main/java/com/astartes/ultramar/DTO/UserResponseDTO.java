package com.astartes.ultramar.DTO;

public record UserResponseDTO(
        Integer id,
        String username,
        Long roleId,
        String roleName
) {}