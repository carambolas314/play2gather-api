package com.play2gather.iam.domain.port.in;

import com.play2gather.iam.common.dto.request.OAuth2LoginRequest;
import com.play2gather.iam.common.dto.response.TokenResponse;

public interface OAuth2LoginUseCase {
    TokenResponse loginWithOAuth2(OAuth2LoginRequest request);
}