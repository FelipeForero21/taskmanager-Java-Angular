package com.luis.tcc.taskmanagerapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "UserSessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "SessionId")
    private UUID sessionId;
    
    @Column(name = "UserId", nullable = false)
    private UUID userId;
    
    @Column(name = "TokenHash", nullable = false, length = 500)
    private String tokenHash;
    
    @Column(name = "DeviceInfo", length = 500)
    private String deviceInfo;
    
    @Column(name = "IpAddress", length = 45)
    private String ipAddress;
    
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "LastUsedAt", nullable = false)
    private LocalDateTime lastUsedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUsedAt = LocalDateTime.now();
    }
} 