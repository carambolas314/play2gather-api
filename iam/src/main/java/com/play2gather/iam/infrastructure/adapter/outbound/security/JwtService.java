package com.play2gather.iam.infrastructure.adapter.outbound.security;

import com.play2gather.iam.infrastructure.adapter.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    private Key getSigningKey(boolean isRefresh) {
        if (isRefresh) {
            return Keys.hmacShaKeyFor(jwtConfig.getRefreshKey().getBytes());
        }
        return getPrivateKey(); // para AccessToken é RSA
    }

    private PrivateKey getPrivateKey() {
        try {
            String rawKey = jwtConfig.getAccessKey();
            String normalizedKey = rawKey
                    .replace("\\n", "\n")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(normalizedKey);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Could not load private key from env", e);
        }
    }

    public String generateAccessToken(String email, List<String> roles, Long id, UUID jti) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id.toString())
                .claim("roles", roles)
                .setId(jti.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration()))
                .signWith(getSigningKey(false)) // Assina com RSA private key
                .compact();
    }

    public String generateRefreshToken(String email, Long id, UUID jti) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id.toString())
                .setId(jti.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiration()))
                .signWith(getSigningKey(true)) // Assina com HMAC shared secret
                .compact();
    }

    public Claims validateAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(false))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims validateRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(true))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromAccessToken(String token) {
        return validateAccessToken(token).getSubject();
    }

    public String getEmailFromRefreshToken(String token) {
        return validateRefreshToken(token).getSubject();
    }

    public Long getIdFromAccessToken(String token) {
        return Long.parseLong(validateAccessToken(token).get("id", String.class));
    }

    public String getTokenIdFromRefreshToken(String token) {
        return validateRefreshToken(token).getId();
    }

    public String getTokenIdFromAccessToken(String token) {
        return validateAccessToken(token).getId();
    }

    public Long getIdFromRefreshToken(String token) {
        return Long.parseLong(validateRefreshToken(token).get("id", String.class));
    }

    public List getRolesFromAccessToken(String token) {
        return validateAccessToken(token).get("roles", List.class);
    }

    public boolean isAdmin(String token) {
        return getRolesFromAccessToken(token).contains("ROLE_ADMIN");
    }
}
