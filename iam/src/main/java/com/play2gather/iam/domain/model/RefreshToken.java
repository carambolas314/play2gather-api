package com.play2gather.iam.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {
    private UUID id = UUID.randomUUID();
    private final Long userId;
    private final LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
    private boolean revoked = false;
    private String token;

    public RefreshToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public RefreshToken(Long userId, String token, UUID id) {
        this.userId = userId;
        this.token = token;
        this.id = id;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revoked;
    }

    public UUID getId() { return id; }
    public Long getUserId() { return userId; }
    public String getToken() { return  token;}
    public String getValue() { return id.toString(); }
    public void revoke() { this.revoked = true; }
}
