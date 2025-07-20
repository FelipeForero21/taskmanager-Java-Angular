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
public class TaskResponse {
    
    private UUID taskId;
    
    private String title;
    
    private String description;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private boolean isActive;
    
    private PriorityInfo priority;
    
    private StatusInfo status;
    
    private CategoryInfo category;
    
    private UserInfo assignedTo;
    
    private UserInfo createdBy;
    
    private boolean isOverdue;
    
    private long daysUntilDue;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityInfo {
        private Integer priorityId;
        
        private String priorityName;
        
        private Integer priorityLevel;
        
        private String colorHex;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusInfo {
        private Integer statusId;
        
        private String statusName;
        
        private String colorHex;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Integer categoryId;
        
        private String categoryName;
        
        private String colorHex;
        
        private String iconName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private UUID userId;
        
        private String email;
        
        private String firstName;
        
        private String lastName;
    }
} 