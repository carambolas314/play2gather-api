package com.play2gather.contracts.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntryRequest {
    private String traceId;
    private String service;
    private String path;
    private String method;
    private Integer status;
    private Long durationMs;
    private Instant timestamp;
    private LogLevel level;
    private String message;
}