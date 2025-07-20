package com.luis.tcc.taskmanagerapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "UserPreferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PreferenceId")
    private UUID preferenceId;
    
    @Column(name = "UserId", nullable = false, unique = true)
    private UUID userId;
    
    @Column(name = "Theme", nullable = false, length = 20)
    private String theme = "light"; // 'light', 'dark', 'auto'
    
    @Column(name = "Language", nullable = false, length = 10)
    private String language = "es-CO";
    
    @Column(name = "NotificationsEnabled", nullable = false)
    private Boolean notificationsEnabled = true;
    
    @Column(name = "EmailNotifications", nullable = false)
    private Boolean emailNotifications = true;
    
    @Column(name = "TasksPerPage", nullable = false)
    private Integer tasksPerPage = 10;
    
    @Column(name = "DefaultTaskView", nullable = false, length = 20)
    private String defaultTaskView = "list"; // 'list', 'grid', 'kanban'
    
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 