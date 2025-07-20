package com.luis.tcc.taskmanagerapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TaskId")
    private UUID taskId;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    @Column(name = "Title", nullable = false, length = 200)
    private String title;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskStatusId", nullable = false)
    private TaskStatus taskStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskPriorityId", nullable = false)
    private TaskPriority taskPriority;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryId")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CreatedBy", nullable = false)
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AssignedTo")
    private User assignedTo;
    
    @Column(name = "DueDate")
    private LocalDateTime dueDate;
    
    @Column(name = "CompletedAt")
    private LocalDateTime completedAt;
    
    @Column(name = "EstimatedHours", precision = 5, scale = 2)
    private BigDecimal estimatedHours;
    
    @Column(name = "ActualHours", precision = 5, scale = 2)
    private BigDecimal actualHours;
    
    @Column(name = "IsDeleted", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Método helper para verificar si la tarea está vencida
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now()) && 
               !"Completada".equals(taskStatus.getStatusName());
    }
    
    public boolean isDueSoon() {
        if (dueDate == null || "Completada".equals(taskStatus.getStatusName())) {
            return false;
        }
        LocalDateTime nextWeek = LocalDateTime.now().plusDays(7);
        return dueDate.isBefore(nextWeek) && dueDate.isAfter(LocalDateTime.now());
    }
} 