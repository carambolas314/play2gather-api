package com.play2gather.iam.domain.port.in;

import com.play2gather.iam.common.dto.request.UserUpdateRequest;
import com.play2gather.iam.domain.model.User;

public interface UpdateUserUseCase {
    User update(Long userId, UserUpdateRequest user);
}
