package com.play2gather.iam.application.service;

import com.play2gather.iam.application.exception.CustomException;
import com.play2gather.iam.application.exception.ResourceNotFoundException;
import com.play2gather.iam.common.dto.request.LoginRequest;
//import com.play2gather.iam.common.dto.request.OAuth2LoginRequest;
import com.play2gather.iam.common.dto.response.TokenResponse;
import com.play2gather.iam.domain.model.RefreshToken;
import com.play2gather.iam.domain.model.User;
import com.play2gather.iam.domain.port.in.*;
import com.play2gather.iam.domain.port.out.RefreshTokenRepositoryPort;
import com.play2gather.iam.domain.port.out.UserRepositoryPort;
import com.play2gather.iam.infrastructure.adapter.outbound.security.JwtService;
import com.play2gather.iam.infrastructure.adapter.outbound.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static com.play2gather.iam.application.exception.ExceptionFlags.DANGER;

@Service
public class AuthService implements LoginUseCase, RefreshTokenUseCase, LogoutUseCase {

    @Autowired
    private UserRepositoryPort userRepository;

    @Autowired
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Autowired
    private JwtService jwtProvider;

    @Override
    public TokenResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não identificado"));

        if (!PasswordUtil.checkPassword(request.getPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED.value(), "Email ou senha incorretos", "Tente novamente ou volte mais tarde.");
        }

        UUID tokenId = UUID.randomUUID();
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail(), user.getId(), tokenId);
        RefreshToken token = new RefreshToken(user.getId(), refreshToken, tokenId);

        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRoles(), user.getId(), tokenId);

        refreshTokenRepository.save(token);

        return new TokenResponse(accessToken, token.getToken(), user.getId());
    }

//    @Override
//    public TokenResponse loginWithOAuth2(OAuth2LoginRequest request) {
//        // mock OAuth2 user validation
//        UUID userId = UUID.nameUUIDFromBytes(("oauth2:" + request.getProviderUserId()).getBytes());
//        RefreshToken accessToken = new RefreshToken(userId);
//        refreshTokenRepository.save(accessToken);
//        return new TokenResponse(jwtProvider.generateAccessToken(userId.toString()), accessToken.getValue());
//    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        RefreshToken token = getRefreshToken(refreshToken);

        try {
            jwtProvider.validateRefreshToken(token.getToken());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT inválido");
        }

        if (token.isExpired() || token.isRevoked()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Seu accessToken foi revogado ou expirou, por favor, faça login novamente."
            );
        }

        refreshTokenRepository.revoke(token.getId());

        String userEmail = jwtProvider.getEmailFromRefreshToken(token.getToken());
        Long userID = jwtProvider.getIdFromRefreshToken(token.getToken());

        UUID tokenId = UUID.randomUUID();
        String newRefreshToken = jwtProvider.generateRefreshToken(userEmail, userID, tokenId);
        RefreshToken newToken = new RefreshToken(userID, newRefreshToken, tokenId);
        refreshTokenRepository.save(newToken);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(
                        HttpStatus.NOT_FOUND.value(),
                        "Usuário não encontrado",
                        "Por favor, tente novamente mais tarde.",
                        DANGER));

        List<String> roles = user.getRoles();
        String newAccessToken = jwtProvider.generateAccessToken(userEmail, roles, user.getId(), tokenId);

        return new TokenResponse(newAccessToken, newToken.getToken(), user.getId());
    }


    @Override
    public void logout(String refreshToken) {
        RefreshToken token = getRefreshToken(refreshToken);
        refreshTokenRepository.revoke(token.getId());

    }

    public RefreshToken getRefreshToken(String refreshToken) {
        String tokenId = jwtProvider.getTokenIdFromRefreshToken(refreshToken);
        return refreshTokenRepository.findByTokenId(tokenId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Token de refresh não está registrado no sistema"
        ));
    }
}
