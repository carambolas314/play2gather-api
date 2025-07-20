package com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.repository;

import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserRoleEntity;
import com.play2gather.iam.infrastructure.adapter.outbound.persistence.User.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleJpaRespository extends JpaRepository<UserRoleEntity, UserRoleId> {
}
