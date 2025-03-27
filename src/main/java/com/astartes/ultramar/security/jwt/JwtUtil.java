package com.astartes.ultramar.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtUtil {

    private final String secretKey;

    private static final long ACCESS_TOKEN_EXPIRATION = 60000; // 1 minute
    private static final long REFRESH_TOKEN_EXPIRATION = 600000; // 10 minutes

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    // Extrait tous les claims du token
    private Claims extractAllClaims(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("Token expiré : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Token invalide : {}", e.getMessage());
            throw new RuntimeException("Invalid JWT", e);
        }
    }

    // Extrait le nom d'utilisateur (subject) du token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Vérifie si le token a expiré
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Valide le token en comparant le username et en vérifiant l'expiration
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Génère un token à partir de l'Authentication (utilisé lors du login)
    public String generateAccessToken(Authentication authentication, String role) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("roles", role);
        claims.put("iat", new Date());
        claims.put("exp", new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("iat", new Date());
        claims.put("exp", new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION));
        claims.put("token_type", "refresh");
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();
    }

    // Rafraîchit un token existant (en réinitialisant la date d'émission et d'expiration)
    public String refreshAccessToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            if (!"refresh".equals(claims.get("token_type"))) {
                throw new RuntimeException("Ce token n'est pas un refresh token");
            }
            Map<String, Object> updatedClaims = new HashMap<>(claims);
            updatedClaims.remove("token_type");
            updatedClaims.put("iat", new Date());
            updatedClaims.put("exp", new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            return Jwts.builder()
                    .claims(updatedClaims)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token", e);
        }
    }

    public String refreshRefreshToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            if (!"refresh".equals(claims.get("token_type"))) {
                throw new RuntimeException("Ce token n'est pas un refresh token");
            }
            Map<String, Object> updatedClaims = new HashMap<>(claims);
            updatedClaims.put("iat", new Date());
            updatedClaims.put("exp", new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION));
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            updatedClaims.put("token_type", "refresh");
            return Jwts.builder()
                    .claims(updatedClaims)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token", e);
        }
    }


}