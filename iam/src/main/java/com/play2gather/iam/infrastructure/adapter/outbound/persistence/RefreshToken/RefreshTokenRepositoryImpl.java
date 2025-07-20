package com.play2gather.iam.infrastructure.adapter.outbound.persistence.RefreshToken;

import com.play2gather.iam.domain.model.RefreshToken;
import com.play2gather.iam.domain.port.out.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpa;

    public RefreshTokenRepositoryImpl(RefreshTokenJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public RefreshTokenEntity save(RefreshToken token) {
        return jpa.save(RefreshTokenEntity.fromDomain(token));
    }

    @Override
    public Optional<RefreshToken> findByTokenId(String tokenId) {
        return jpa.findById(UUID.fromString(tokenId)).map(RefreshTokenEntity::toDomain);
    }

    @Override
    public void deleteAll(){
        jpa.deleteAllByRevokedTrue();
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return jpa.findByUserId(userId)
                .map(RefreshTokenEntity::toDomain);
    }

    @Override
    public boolean isRevoked(UUID id){
        return jpa.existsByIdAndRevokedTrue(id);
    }

    @Override
    public void revoke(UUID id) {
        jpa.findById(id).ifPresent(entity -> {
            entity.setRevoked(true);
            jpa.save(entity);
        });
    }
}