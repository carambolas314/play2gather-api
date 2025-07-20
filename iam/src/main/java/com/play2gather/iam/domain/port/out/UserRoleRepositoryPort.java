package com.play2gather.iam.domain.port.out;

import com.play2gather.iam.domain.model.UserRole;

import java.util.Optional;

public interface UserRoleRepositoryPort {
    UserRole save(UserRole userRole);
}

