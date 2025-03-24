package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.RoleDTO;
import com.astartes.ultramar.DTO.UserCreateDTO;
import com.astartes.ultramar.DTO.UserDTO;
import com.astartes.ultramar.DTO.UserResponseDTO;
import com.astartes.ultramar.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUltramarine(@Valid @RequestBody UserCreateDTO userDTO) {
        UserResponseDTO saved = userService.createUser(userDTO.username(), userDTO.password(), userDTO.roleId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/roles")
    public List<RoleDTO> getAllRole() {
        return userService.getAllRole();
    }

}
