package com.play2gather.iam.domain.port.in;

import com.play2gather.iam.common.dto.request.LoginRequest;
import com.play2gather.iam.common.dto.response.TokenResponse;

public interface LoginUseCase {
    TokenResponse login(LoginRequest request);
}