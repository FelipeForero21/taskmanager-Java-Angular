package com.luis.tcc.taskmanagerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    
    @Builder.Default
    private String tokenType = "Bearer";
    
    private Long expiresIn;
    
    private UserInfo user;
    
    private String message;
    
    private LocalDateTime timestamp;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private UUID userId;
        
        private String email;
        
        private String firstName;
        
        private String lastName;
        
        private String phoneNumber;
        
        private boolean isActive;
        
        private LocalDateTime createdAt;
    }
} 