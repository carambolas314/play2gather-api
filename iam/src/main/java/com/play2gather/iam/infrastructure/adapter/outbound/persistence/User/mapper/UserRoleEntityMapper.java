package com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.mapper;

import com.play2gather.iam.domain.model.UserRole;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserEntity;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserRoleEntity;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserRoleId;

public class UserRoleEntityMapper {

    public static UserRole toDomain(UserRoleEntity entity) {
        if (entity == null || entity.getId() == null) {
            return null;
        }

        UserRole domain = new UserRole();
        domain.setIdUser(entity.getId().getIdUser());
        domain.setRole(entity.getId().getRole());
        return domain;
    }

    public static UserRoleEntity toEntity(UserRole domain) {

        UserEntity userEntity = new UserEntity(domain.getIdUser());

        UserRoleId id = new UserRoleId();
        id.setIdUser(domain.getIdUser());
        id.setRole(domain.getRole());

        UserRoleEntity entity = new UserRoleEntity();
        entity.setId(id);
        entity.setUser(userEntity);
        return entity;
    }
}
