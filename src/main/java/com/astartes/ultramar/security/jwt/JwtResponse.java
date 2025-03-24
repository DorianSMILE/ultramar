package com.astartes.ultramar.security.jwt;

import com.astartes.ultramar.DTO.UserResponseDTO;
import lombok.Data;

@Data
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponseDTO userResponseDTO;

    public JwtResponse(String accessToken, String refreshToken, UserResponseDTO userResponseDTO) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userResponseDTO = userResponseDTO;
    }

    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

