package com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.repository;

import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
