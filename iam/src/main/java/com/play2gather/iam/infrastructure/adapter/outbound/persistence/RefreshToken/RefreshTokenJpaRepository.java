package com.play2gather.iam.infrastructure.adapter.outbound.persistence.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    Optional<RefreshTokenEntity> findByUserId(Long userId);

    @Transactional
    void deleteAllByRevokedTrue();
    boolean existsByIdAndRevokedTrue(UUID id);
}

