package com.play2gather.iam.common.dto.response;

import lombok.Data;

@Data
public class TokenResponse {
    private Long userId;
    private String accessToken;
    private String refreshToken;

    public TokenResponse(String accessToken, String refreshToken, Long userId) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
