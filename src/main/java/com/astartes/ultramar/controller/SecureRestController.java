package com.astartes.ultramar.controller;

import com.astartes.ultramar.DTO.UserDTO;
import com.astartes.ultramar.DTO.UserResponseDTO;
import com.astartes.ultramar.security.jwt.JwtResponse;
import com.astartes.ultramar.security.jwt.JwtUtil;
import com.astartes.ultramar.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    // Endpoint de login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO userDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDTO.username(),
                            userDTO.password()
                    )
            );
            String accessToken = jwtUtil.generateAccessToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(authentication);
            UserResponseDTO userResponseDTO = userService.getUserByName(userDTO.username());
            return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken, userResponseDTO));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Échec de l'authentification");
        }
    }

    // Endpoint pour rafraîchir le token
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        System.out.println(request);
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