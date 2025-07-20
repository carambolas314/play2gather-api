package com.play2gather.iam.domain.port.out;

import com.play2gather.iam.domain.model.RefreshToken;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.RefreshToken.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {
    RefreshTokenEntity save(RefreshToken token);
    Optional<RefreshToken> findByTokenId(String tokenId);
    Optional<RefreshToken> findByUserId(Long userId);
    void deleteAll();

    boolean isRevoked(UUID id);

    void revoke(UUID id);
}
