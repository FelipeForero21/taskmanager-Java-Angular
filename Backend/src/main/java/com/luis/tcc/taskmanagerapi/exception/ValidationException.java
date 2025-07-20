package com.luis.tcc.taskmanagerapi.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    
    private final String field;
    private final String value;
    
    public ValidationException(String message) {
        super(message);
        this.field = "N/A";
        this.value = "N/A";
    }
    
    public ValidationException(String message, String field, String value) {
        super(message);
        this.field = field;
        this.value = value;
    }
    
    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
        this.value = "N/A";
    }
} 