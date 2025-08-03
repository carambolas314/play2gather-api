package com.play2gather.iam.infrastructure.adapter.inbound.web;

import com.play2gather.iam.common.dto.request.LoginRequest;
import com.play2gather.iam.common.dto.response.LoginResponse;
import com.play2gather.iam.common.dto.response.TokenResponse;
import com.play2gather.iam.domain.port.in.LoginUseCase;
import com.play2gather.iam.domain.port.in.LogoutUseCase;
import com.play2gather.iam.domain.port.in.RefreshTokenUseCase;
import com.play2gather.iam.infrastructure.adapter.config.CookieProperties;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/iam/auth")
public class AuthController {
    @Autowired
    private RefreshTokenUseCase refreshTokenUseCase;
    @Autowired
    private LoginUseCase loginUseCase;
    @Autowired
    private LogoutUseCase logoutUseCase;
    @Autowired
    private CookieProperties cookieProperties;

    //private final OAuth2LoginUseCase oAuth2LoginUseCase;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {

        TokenResponse tokenResponse = loginUseCase.login(request);

        LoginResponse loginResponse = new LoginResponse(tokenResponse.getUserId().toString(), tokenResponse.getAccessToken());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(cookieProperties.isSecure()) // mudar para true depois
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite(cookieProperties.getSameSite())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken,
                                                 HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh accessToken não fornecido");
        }

        TokenResponse tokenResponse = refreshTokenUseCase.refresh(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false) // mudar para true depois
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        LoginResponse refreshResponse = new LoginResponse(tokenResponse.getUserId().toString(), tokenResponse.getAccessToken());

        return new ResponseEntity<>(refreshResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token não fornecido para logout");
        }

        logoutUseCase.logout(refreshToken);
        return ResponseEntity.accepted().build();
    }

//    @PostMapping("/oauth2")
//    public ResponseEntity<TokenResponse> oauth2(@RequestBody OAuth2LoginRequest request) {
//        return ResponseEntity.ok(oAuth2LoginUseCase.loginWithOAuth2(request));
//    }
}