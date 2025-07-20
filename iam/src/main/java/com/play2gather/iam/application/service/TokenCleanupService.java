package com.play2gather.iam.application.service;

import com.play2gather.iam.domain.port.out.RefreshTokenRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenCleanupService {

    @Autowired
    private RefreshTokenRepositoryPort refreshTokenRepository;


    @Scheduled(cron = "0 0 0 * * ?") // meia-noite
    public void deleteRevokedTokens() {
        refreshTokenRepository.deleteAll();
        System.out.println("[CRON] Tokens revogados deletados com sucesso.");
    }
}

