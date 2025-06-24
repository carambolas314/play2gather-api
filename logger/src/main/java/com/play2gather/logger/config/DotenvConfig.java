package com.play2gather.logger.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DotenvConfig {

    @Value("${app.use-dotenv:true}")
    private boolean useDotenv;

    @PostConstruct
    public void init() {
        if (useDotenv) {
            log.info("🔁 Carregando variáveis do arquivo .env");
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        } else {
            log.info("🚫 Carregamento do .env desativado via configuração.");
        }
    }
}