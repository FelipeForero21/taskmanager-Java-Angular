package com.luis.tcc.taskmanagerapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TaskStatuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaskStatusId")
    private Integer taskStatusId;
    
    @Column(name = "StatusName", nullable = false, unique = true, length = 50)
    private String statusName;
    
    @Column(name = "StatusDescription", length = 200)
    private String statusDescription;
    
    @Column(name = "ColorHex", nullable = false, length = 7)
    private String colorHex = "#6c757d";
    
    @Column(name = "SortOrder", nullable = false)
    private Integer sortOrder = 0;
    
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 