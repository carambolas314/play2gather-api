package com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.repository;

import com.play2gather.iam.domain.model.UserRole;
import com.play2gather.iam.domain.port.out.UserRoleRepositoryPort;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.mapper.UserRoleEntityMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepositoryImpl implements UserRoleRepositoryPort {
    private final UserRoleJpaRespository jpa;

    public UserRoleRepositoryImpl(UserRoleJpaRespository jpa) {
        this.jpa = jpa;
    }

    @Override
    public UserRole save(UserRole userRole) {
        return jpa.save(UserRoleEntityMapper.toEntity(userRole)).toDomain();
    }
}