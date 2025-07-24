package com.play2gather.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Service
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    public JwtService() {
    }

    private PublicKey loadPublicKey() {
        try {
            String rawKey = jwtConfig.getPublicKey();
            String normalizedKey = rawKey
                    .replace("\\n", "\n")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(normalizedKey);
            return KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(decoded));

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar a chave pública", e);
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(loadPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getIdFromAccessToken(String token) {
        return Long.parseLong(parseToken(token).get("id", String.class));
    }

    public String getEmailFromAccessToken(String token) {
        return parseToken(token).getSubject();
    }

    public List<String> getRolesFromAccessToken(String token) {
        return parseToken(token).get("roles", List.class);
    }
}
