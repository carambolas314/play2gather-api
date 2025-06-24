package com.play2gather.logger.controller;

import com.play2gather.logger.model.LogEntry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Desativado para evitar execução automática durante build")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testLogEntryEndpoint() {
        LogEntry request = LogEntry.builder()
                .traceId("test-trace-id")
                .service("test-service")
                .path("/test/path")
                .method("GET")
                .status(200)
                .durationMs(123L)
                .timestamp(Instant.now())
                .level(null)
                .message("Test message")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LogEntry> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("/logs", entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }
}
