package com.luis.tcc.taskmanagerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    
    private UserInfo user;
    private TaskStats taskStats;
    private List<TaskSummary> recentTasks;
    private List<TaskSummary> upcomingTasks;
    private List<CategoryStats> categoryStats;
    private ProductivityMetrics productivityMetrics;
    private LocalDateTime lastUpdated;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private UUID userId;
        private String email;
        private String firstName;
        private String lastName;
        private String fullName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskStats {
        private long totalTasks;
        private long pendingTasks;
        private long inProgressTasks;
        private long completedTasks;
        private long overdueTasks;
        private long todayTasks;
        private long thisWeekTasks;
        private long thisMonthTasks;
        private double completionRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskSummary {
        private UUID taskId;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private LocalDateTime createdAt;
        private String priorityName;
        private String statusName;
        private String categoryName;
        private String priorityColor;
        private String statusColor;
        private String categoryColor;
        private boolean isOverdue;
        private long daysUntilDue;
        private UserInfo assignedTo;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStats {
        private Integer categoryId;
        private String categoryName;
        private String colorHex;
        private String iconName;
        private long taskCount;
        private double percentage;
        private long completedTasks;
        private long pendingTasks;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductivityMetrics {
        private double averageCompletionTime;
        private double tasksPerDay;
        private double onTimeCompletionRate;
        private int streakDays;
        private String productivityLevel;
        private List<DailyProgress> weeklyProgress;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyProgress {
        private String date;
        private long completedTasks;
        private long totalTasks;
        private double completionRate;
    }
} 