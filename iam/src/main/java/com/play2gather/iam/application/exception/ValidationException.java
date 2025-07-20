package com.play2gather.iam.application.exception;

import com.play2gather.iam.infrastructure.adapter.inbound.exception.ValidationError;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<ValidationError> errors; // Lista de erros de validação

    public ValidationException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
