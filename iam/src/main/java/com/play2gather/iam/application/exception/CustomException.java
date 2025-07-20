package com.play2gather.iam.application.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int status; // Código de erro HTTP
    private final String details; // Detalhes personalizados
    private final ExceptionFlags flag; // Detalhes personalizados

    public CustomException(int status, String message, String details) {
        super(message);
        this.status = status;
        this.details = details;
        this.flag = ExceptionFlags.DANGER;
    }

    public CustomException(int status, String message, String details, ExceptionFlags flag) {
        super(message);
        this.status = status;
        this.details = details;
        this.flag = flag;
    }
}