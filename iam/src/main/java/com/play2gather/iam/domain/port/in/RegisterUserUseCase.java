package com.play2gather.iam.domain.port.in;

import com.play2gather.iam.common.dto.request.UserRegisterRequest;
import com.play2gather.iam.domain.model.User;

public interface RegisterUserUseCase {
    User register(UserRegisterRequest user);
}