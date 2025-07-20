package com.play2gather.iam.domain.port.in;

import com.play2gather.iam.domain.model.User;

public interface ProfileUserUseCase {
    User getProfile(Long userId);
}
