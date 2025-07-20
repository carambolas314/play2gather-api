package com.play2gather.iam.domain.port.in;

public interface LogoutUseCase {
    void logout(String refreshToken);
}
