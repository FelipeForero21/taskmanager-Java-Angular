package com.luis.tcc.taskmanagerapi.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceName;
    private final String resourceId;
    
    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(String.format("%s no encontrado con id: %s", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
    
    public ResourceNotFoundException(String resourceName, Long resourceId) {
        this(resourceName, String.valueOf(resourceId));
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "Recurso";
        this.resourceId = "N/A";
    }
} 