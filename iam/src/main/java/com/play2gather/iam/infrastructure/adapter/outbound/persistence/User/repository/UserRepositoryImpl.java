package com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.repository;

import com.play2gather.iam.domain.model.User;
import com.play2gather.iam.domain.port.out.UserRepositoryPort;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserEntity;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.mapper.UserEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepositoryPort {
    private final UserJpaRepository jpa;

    public UserRepositoryImpl(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public User save(User user) {
        return jpa.save(UserEntityMapper.toEntity(user)).toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id).map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email).map(UserEntity::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}