package com.play2gather.iam.infrastructure.adapter.inbound.exception;


import com.play2gather.iam.application.exception.ExceptionFlags;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String details;
    private String path;
    private ExceptionFlags flag;

    // Construtor
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String details,ExceptionFlags flag, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.path = path;
        this.flag = flag;
    }
}