package com.play2gather.iam.infrastructure.adapter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    private String accessKey;
    private String refreshKey;
    private long accessTokenExpiration;   // e.g., 15 * 60 * 1000L (15 min)
    private long refreshTokenExpiration;  // e.g., 7 * 24 * 60 * 60 * 1000L (7 dias)
}