package com.luis.tcc.taskmanagerapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TaskPriorities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskPriority {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaskPriorityId")
    private Integer taskPriorityId;
    
    @Column(name = "PriorityName", nullable = false, unique = true, length = 50)
    private String priorityName;
    
    @Column(name = "PriorityDescription", length = 200)
    private String priorityDescription;
    
    @Column(name = "PriorityLevel", nullable = false)
    private Integer priorityLevel;
    
    @Column(name = "ColorHex", nullable = false, length = 7)
    private String colorHex = "#6c757d";
    
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 