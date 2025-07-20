package com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity;

import com.play2gather.iam.domain.model.User;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.mapper.UserEntityMapper;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "iam_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRoleEntity> roles = new HashSet<>();

    public UserEntity() {
    }

    public UserEntity(Long id) {
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<UserRoleEntity> getRoles() { return roles; }
    public void setRoles(Set<UserRoleEntity> roles) { this.roles = roles; }

    public User toDomain() {
        return UserEntityMapper.toDomain(this);
    }
}