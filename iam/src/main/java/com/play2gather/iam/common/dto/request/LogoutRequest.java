package com.play2gather.iam.common.dto.request;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}