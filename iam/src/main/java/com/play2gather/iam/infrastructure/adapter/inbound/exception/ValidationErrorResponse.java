package com.play2gather.iam.infrastructure.adapter.inbound.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<ValidationError> errors; // Lista de erros de validação
    private String path;

    // Construtor
    public ValidationErrorResponse(LocalDateTime timestamp, int status, String error, String message, List<ValidationError> errors, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.errors = errors;
        this.path = path;
    }
}
