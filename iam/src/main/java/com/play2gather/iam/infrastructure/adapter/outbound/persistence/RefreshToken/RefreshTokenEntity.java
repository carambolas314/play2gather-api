package com.play2gather.iam.infrastructure.adapter.outbound.persistence.RefreshToken;

import com.play2gather.iam.domain.model.RefreshToken;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "session")
public class RefreshTokenEntity {
    @Id
    private UUID id;
    private Long userId;
    private LocalDateTime expiresAt;
    private boolean revoked;
    private String token;

    public static RefreshTokenEntity fromDomain(RefreshToken token) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.id = token.getId();
        entity.userId = token.getUserId();
        entity.expiresAt = LocalDateTime.now().plusDays(7);
        entity.revoked = token.isRevoked();
        entity.token = token.getToken();
        return entity;
    }

    public RefreshToken toDomain() {
        RefreshToken token = new RefreshToken(this.userId, this.token, this.id);
        if (this.revoked) token.revoke();
        return token;
    }

    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}