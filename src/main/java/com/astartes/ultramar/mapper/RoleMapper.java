package com.astartes.ultramar.mapper;

import com.astartes.ultramar.DTO.RoleDTO;
import com.astartes.ultramar.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleDTO toDTO(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleDTO(role.getId(), role.getRole());
    }

    public Role toEntity(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }
        Role role = new Role();
        role.setRole(roleDTO.role());
        return role;
    }
}
