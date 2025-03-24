package com.astartes.ultramar.mapper;

import com.astartes.ultramar.DTO.UserResponseDTO;
import com.astartes.ultramar.entity.User;

public class UserMapper {
    public static UserResponseDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().getId(),
                user.getRole().getRole()
        );
    }
}
