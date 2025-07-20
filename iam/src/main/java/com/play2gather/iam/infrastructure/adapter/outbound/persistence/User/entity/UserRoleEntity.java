package com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity;

import com.play2gather.iam.domain.model.UserRole;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.mapper.UserRoleEntityMapper;
import jakarta.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRoleEntity {
    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public UserRoleId getId() {
        return id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getRole() {
        return id.getRole();
    }

    public void setRole(String role) {
        this.id.setRole(role);
    }

    public UserRole toDomain() {
        return UserRoleEntityMapper.toDomain(this);
    }

}