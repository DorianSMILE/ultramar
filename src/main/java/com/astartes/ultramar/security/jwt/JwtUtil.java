package com.astartes.ultramar.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtUtil {

    private static final String SECRET_KEY = "0e6e1158bdee72d6437940f9db1768a0abf9284f67901f3de18f5f634a1734ffb62226f36613c72e7b6a79f5b8689aa99d4e515e6505457bb2556321a49d010e";

    private static final long ACCESS_TOKEN_EXPIRATION = 60000; // 1 minute
    private static final long REFRESH_TOKEN_EXPIRATION = 600000; // 10 minutes


    // Extrait tous les claims du token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
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
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .claim("token_type", "refresh")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Rafraîchit un token existant (en réinitialisant la date d'émission et d'expiration)
    public String refreshAccessToken(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            if (!"refresh".equals(claims.get("token_type"))) {
                throw new RuntimeException("Ce token n'est pas un refresh token");
            }
            claims.remove("token_type");
            claims.setIssuedAt(new Date());
            claims.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION));
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(key, SignatureAlgorithm.HS512)
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
            claims.setIssuedAt(new Date());
            claims.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION));
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            claims.put("token_type", "refresh"); //explicite car déjà censé y être
            return Jwts.builder()
                    .setClaims(claims)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token", e);
        }
    }

}