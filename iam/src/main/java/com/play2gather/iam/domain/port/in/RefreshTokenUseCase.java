package com.play2gather.iam.domain.port.in;

import com.play2gather.iam.common.dto.response.TokenResponse;

public interface RefreshTokenUseCase {
    TokenResponse refresh(String refreshToken);
}
