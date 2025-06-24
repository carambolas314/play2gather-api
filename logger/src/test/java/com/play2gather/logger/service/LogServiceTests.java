package com.play2gather.logger.service;

import com.play2gather.logger.model.LogEntry;
import com.play2gather.logger.repository.LogRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Desativado para evitar execução automática durante build")
@SpringBootTest
public class LogServiceTests {

    @Autowired
    private LogService loggerService;

    @Autowired
    private LogRepository repository;

    @Test
    void testSaveLogEntry() {
        String traceId = UUID.randomUUID().toString();
        LogEntry req = LogEntry.builder()
                .traceId(traceId)
                .service("service-a")
                .path("/some/path")
                .method("POST")
                .status(201)
                .durationMs(456L)
                .timestamp(Instant.now())
                .message("Service log test")
                .build();

        loggerService.saveLog(req);
        LogEntry entries = repository.findByTraceId(traceId);
        assertThat(entries).isNotNull();
        assertThat(entries.getMessage()).isEqualTo("Service log test");
    }
}
