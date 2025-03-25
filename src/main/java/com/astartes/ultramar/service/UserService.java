package com.astartes.ultramar.service;

import com.astartes.ultramar.DTO.RoleDTO;
import com.astartes.ultramar.DTO.UserResponseDTO;
import com.astartes.ultramar.entity.Role;
import com.astartes.ultramar.entity.User;
import com.astartes.ultramar.exception.UserNotFoundException;
import com.astartes.ultramar.exception.UserRoleException;
import com.astartes.ultramar.mapper.RoleMapper;
import com.astartes.ultramar.mapper.UserMapper;
import com.astartes.ultramar.repository.RoleRepository;
import com.astartes.ultramar.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(String username, String rawPassword, Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserRoleException(roleId));
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }

    public List<RoleDTO> getAllRole() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserByName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.toDTO(user);
    }

    // pour aff tout les users plus tard
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}
