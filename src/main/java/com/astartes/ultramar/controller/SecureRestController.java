package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.UserLoginDTO;
import com.astartes.ultramar.DTO.UserResponseDTO;
import com.astartes.ultramar.security.jwt.JwtResponse;
import com.astartes.ultramar.security.jwt.JwtUtil;
import com.astartes.ultramar.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public SecureRestController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // Endpoint de login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginDTO.username(),
                            userLoginDTO.password()
                    )
            );
            UserResponseDTO userResponseDTO = userService.getUserByName(userLoginDTO.username());
            String accessToken = jwtUtil.generateAccessToken(authentication, userResponseDTO.roleName());
            String refreshToken = jwtUtil.generateRefreshToken(authentication);
            return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, userResponseDTO));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Échec de l'authentification");
        }
    }

    // Endpoint pour rafraîchir le token
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refreshToken = authHeader.substring(7);
            String newAccessToken = jwtUtil.refreshAccessToken(refreshToken);
            String newRefreshToken = jwtUtil.refreshRefreshToken(refreshToken);
            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
        }
        return ResponseEntity.badRequest().body("En-tête Authorization manquant ou invalide");
    }
}