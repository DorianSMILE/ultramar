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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, EmailService emailService, RoleMapper roleMapper, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponseDTO createUser(String username, String rawPassword, Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserRoleException(roleId));
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setUUID(UUID.randomUUID());
        User savedUser = userRepository.save(user);

        emailService.sendEmail(savedUser.getUUID());

        return userMapper.toDTO(savedUser);
    }

    public List<RoleDTO> getAllRole() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserByName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return userMapper.toDTO(user);
    }

    public Optional<UUID> verifUuidExist(UUID token) {
        return userRepository.findByUUID(token)
                .map(User::getUUID);
    }

    @Transactional
    public void changePassword(UUID uuid, String password) {
        Optional<User> optionalUser = userRepository.findByUUID(uuid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(password));
            user.setUUID(null);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException(uuid.toString());
        }
    }

}
