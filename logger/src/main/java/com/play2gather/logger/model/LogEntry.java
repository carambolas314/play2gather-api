package com.play2gather.logger.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "logs")
public class LogEntry {
    private String traceId;
    private String service;
    private String level;
    private String message;
    private Instant timestamp;
    private String path;
    private String method;
    private int status;
    private long durationMs;
}

