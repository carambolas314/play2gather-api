package com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.mapper;

import com.play2gather.iam.domain.model.User;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserEntity;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserRoleEntity;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserRoleId;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserEntityMapper {
    public static UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());

        Set<UserRoleEntity> roles = domain.getRoles().stream().map(role -> {
            UserRoleEntity userRole = new UserRoleEntity();
            UserRoleId roleId = new UserRoleId();
            roleId.setIdUser(domain.getId());
            roleId.setRole(role);
            userRole.setId(roleId);
            return userRole;
        }).collect(Collectors.toSet());

        entity.setRoles(roles);
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        List<String> roles = entity.getRoles().stream()
                .map(roleEntity -> roleEntity.getId().getRole())
                .collect(Collectors.toList());

        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                roles
        );
    }
}