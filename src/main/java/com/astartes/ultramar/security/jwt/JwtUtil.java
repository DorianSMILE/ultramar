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

    public static final String IAT = "iat";
    public static final String EXP = "exp";
    public static final String TOKEN_TYPE = "token_type";
    public static final String SUB = "sub";
    public static final String ROLES = "roles";
    public static final String REFRESH = "refresh";
    public static final String NOT_A_REFRESH_TOKEN = "Ce token n'est pas un refresh token";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    public static final String INVALID_JWT = "Invalid JWT";

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
            throw new RuntimeException(INVALID_JWT, e);
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
        claims.put(SUB, userDetails.getUsername());
        claims.put(ROLES, role);
        claims.put(IAT, new Date());
        claims.put(EXP, new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put(SUB, userDetails.getUsername());
        claims.put(IAT, new Date());
        claims.put(EXP, new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION));
        claims.put(TOKEN_TYPE, REFRESH);
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
            if (!REFRESH.equals(claims.get(TOKEN_TYPE))) {
                throw new RuntimeException(NOT_A_REFRESH_TOKEN);
            }
            Map<String, Object> updatedClaims = new HashMap<>(claims);
            updatedClaims.remove(TOKEN_TYPE);
            updatedClaims.put(IAT, new Date());
            updatedClaims.put(EXP, new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            return Jwts.builder()
                    .claims(updatedClaims)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException(INVALID_REFRESH_TOKEN, e);
        }
    }

    public String refreshRefreshToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            if (!REFRESH.equals(claims.get(TOKEN_TYPE))) {
                throw new RuntimeException(NOT_A_REFRESH_TOKEN);
            }
            Map<String, Object> updatedClaims = new HashMap<>(claims);
            updatedClaims.put(IAT, new Date());
            updatedClaims.put(EXP, new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION));
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            updatedClaims.put(TOKEN_TYPE, REFRESH);
            return Jwts.builder()
                    .claims(updatedClaims)
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException(INVALID_REFRESH_TOKEN, e);
        }
    }
}