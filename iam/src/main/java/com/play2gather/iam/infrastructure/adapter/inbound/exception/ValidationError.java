package com.play2gather.iam.infrastructure.adapter.inbound.exception;

import lombok.Data;

@Data
public class ValidationError {
    private String field;
    private String message;

    // Construtor
    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}