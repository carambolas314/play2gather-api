package com.play2gather.iam.common.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String userId;
    private String accessToken;

    public LoginResponse(String userId, String accessToken){
        this.accessToken = accessToken;
        this.userId = userId;
    }

}
