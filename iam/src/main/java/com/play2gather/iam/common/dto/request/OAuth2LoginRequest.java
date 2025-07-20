package com.play2gather.iam.common.dto.request;

import lombok.Data;

@Data
public class OAuth2LoginRequest {
    private String provider;
    private String providerUserId;
}
