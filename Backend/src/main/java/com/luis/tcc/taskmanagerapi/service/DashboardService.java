package com.luis.tcc.taskmanagerapi.service;

import com.luis.tcc.taskmanagerapi.dto.DashboardResponse;
import com.luis.tcc.taskmanagerapi.entity.TaskPriority;
import com.luis.tcc.taskmanagerapi.entity.TaskStatus;
import com.luis.tcc.taskmanagerapi.repository.TaskPriorityRepository;
import com.luis.tcc.taskmanagerapi.repository.TaskRepository;
import com.luis.tcc.taskmanagerapi.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    private final TaskService taskService;
    
    public DashboardResponse getDashboardData(UUID userId) {
        // Obtener estadísticas básicas
        DashboardResponse.TaskStats taskStats = getTaskStats(userId);
        
        // Obtener tareas recientes
        List<DashboardResponse.TaskSummary> recentTasks = getRecentTasksSummary(userId);
        
        // Obtener tareas próximas a vencer
        List<DashboardResponse.TaskSummary> upcomingTasks = getUpcomingTasksSummary(userId);
        
        return DashboardResponse.builder()
                .taskStats(taskStats)
                .recentTasks(recentTasks)
                .upcomingTasks(upcomingTasks)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    private DashboardResponse.TaskStats getTaskStats(UUID userId) {
        // Conteos por estado
        long totalTasks = taskRepository.countTotalTasks(userId);
        long pendingTasks = taskRepository.countByStatus(userId, "Pendiente");
        long inProgressTasks = taskRepository.countByStatus(userId, "En Progreso");
        long completedTasks = taskRepository.countByStatus(userId, "Completada");
        
        // Tareas vencidas
        LocalDateTime now = LocalDateTime.now();
        List<com.luis.tcc.taskmanagerapi.entity.Task> overdueTasks = taskRepository.findOverdueTasks(userId, now);
        long overdueTasksCount = overdueTasks.size();
        
        // Calcular tasa de completitud
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100.0 : 0.0;
        
        return DashboardResponse.TaskStats.builder()
                .totalTasks(totalTasks)
                .pendingTasks(pendingTasks)
                .inProgressTasks(inProgressTasks)
                .completedTasks(completedTasks)
                .overdueTasks(overdueTasksCount)
                .completionRate(completionRate)
                .build();
    }
    
    private List<DashboardResponse.TaskSummary> getRecentTasksSummary(UUID userId) {
        return taskService.getRecentTasks(userId, 5).stream()
                .map(this::convertToTaskSummary)
                .collect(Collectors.toList());
    }
    
    private List<DashboardResponse.TaskSummary> getUpcomingTasksSummary(UUID userId) {
        return taskService.getUpcomingTasks(userId).stream()
                .map(this::convertToTaskSummary)
                .collect(Collectors.toList());
    }
    
    public Map<String, Long> getPriorityDistribution(UUID userId) {
        Map<String, Long> distribution = new HashMap<>();
        
        // Obtener todas las prioridades activas
        List<TaskPriority> priorities = taskPriorityRepository.findByIsActiveTrueOrderByPriorityLevelAsc();
        
        for (TaskPriority priority : priorities) {
            long count = taskRepository.countByPriorityLevel(userId, priority.getPriorityLevel());
            distribution.put(priority.getPriorityName(), count);
        }
        
        return distribution;
    }
    
    public Map<String, Long> getStatusDistribution(UUID userId) {
        Map<String, Long> distribution = new HashMap<>();
        
        // Obtener todos los estados activos
        List<TaskStatus> statuses = taskStatusRepository.findByIsActiveTrueOrderBySortOrderAsc();
        
        for (TaskStatus status : statuses) {
            long count = taskRepository.countByStatus(userId, status.getStatusName());
            distribution.put(status.getStatusName(), count);
        }
        
        return distribution;
    }
    
    private DashboardResponse.TaskSummary convertToTaskSummary(com.luis.tcc.taskmanagerapi.dto.TaskResponse task) {
        return DashboardResponse.TaskSummary.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .priorityName(task.getPriority() != null ? task.getPriority().getPriorityName() : null)
                .statusName(task.getStatus() != null ? task.getStatus().getStatusName() : null)
                .categoryName(task.getCategory() != null ? task.getCategory().getCategoryName() : null)
                .priorityColor(task.getPriority() != null ? task.getPriority().getColorHex() : null)
                .statusColor(task.getStatus() != null ? task.getStatus().getColorHex() : null)
                .categoryColor(task.getCategory() != null ? task.getCategory().getColorHex() : null)
                .isOverdue(task.isOverdue())
                .daysUntilDue(task.getDaysUntilDue())
                .assignedTo(task.getAssignedTo() != null ? DashboardResponse.UserInfo.builder()
                        .userId(task.getAssignedTo().getUserId())
                        .email(task.getAssignedTo().getEmail())
                        .firstName(task.getAssignedTo().getFirstName())
                        .lastName(task.getAssignedTo().getLastName())
                        .build() : null)
                .build();
    }
    
    // Métodos para estadísticas específicas
    public Map<String, Object> getProductivityMetrics(UUID userId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Horas estimadas vs reales
        Object[] hoursSummary = taskRepository.getHoursSummary(userId);
        BigDecimal estimatedHours = (BigDecimal) hoursSummary[0];
        BigDecimal actualHours = (BigDecimal) hoursSummary[1];
        
        metrics.put("estimatedHours", estimatedHours);
        metrics.put("actualHours", actualHours);
        
        if (estimatedHours != null && estimatedHours.compareTo(BigDecimal.ZERO) > 0) {
            double efficiency = actualHours.divide(estimatedHours, 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
            metrics.put("efficiency", efficiency);
        }
        
        // Tareas completadas este mes
        long completedThisMonth = taskRepository.countCompletedTasksLastMonth(userId, LocalDateTime.now().minusMonths(1));
        metrics.put("completedThisMonth", completedThisMonth);
        
        // Tareas vencidas
        LocalDateTime now = LocalDateTime.now();
        List<com.luis.tcc.taskmanagerapi.entity.Task> overdueTasks = taskRepository.findOverdueTasks(userId, now);
        metrics.put("overdueTasks", overdueTasks.size());
        
        return metrics;
    }
    
    public Map<String, Object> getWeeklyProgress(UUID userId) {
        Map<String, Object> weeklyData = new HashMap<>();
        
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        LocalDateTime weekEnd = LocalDateTime.now();
        
        // Tareas completadas esta semana
        long completedThisWeek = taskRepository.countCompletedTasksLastMonth(userId, weekStart);
        weeklyData.put("completedThisWeek", completedThisWeek);
        
        // Tareas creadas esta semana
        List<com.luis.tcc.taskmanagerapi.entity.Task> tasksThisWeek = taskRepository.findByDueDateBetween(userId, weekStart, weekEnd);
        weeklyData.put("createdThisWeek", tasksThisWeek.size());
        
        // Tareas vencidas esta semana
        List<com.luis.tcc.taskmanagerapi.entity.Task> overdueThisWeek = tasksThisWeek.stream()
                .filter(task -> task.isOverdue())
                .collect(Collectors.toList());
        weeklyData.put("overdueThisWeek", overdueThisWeek.size());
        
        return weeklyData;
    }
    
    public Map<String, Object> getCategoryAnalytics(UUID userId) {
        Map<String, Object> analytics = new HashMap<>();

        // Obtener la distribución de tareas por categoría
        List<Object[]> categoriesWithCount = taskRepository.getCategoriesWithTaskCount(userId);
        Map<String, Long> categoryDistribution = new HashMap<>();
        for (Object[] result : categoriesWithCount) {
            com.luis.tcc.taskmanagerapi.entity.Category category = (com.luis.tcc.taskmanagerapi.entity.Category) result[0];
            Long count = (Long) result[1];
            if (category != null) {
                categoryDistribution.put(category.getCategoryName(), count);
            }
        }
        analytics.put("categoryDistribution", categoryDistribution);

        // Obtener las categorías más usadas
        List<Object[]> mostUsedCategoriesRaw = taskRepository.getMostUsedCategories(userId);
        List<Map<String, Object>> mostUsedCategories = new java.util.ArrayList<>();
        for (Object[] result : mostUsedCategoriesRaw) {
            com.luis.tcc.taskmanagerapi.entity.Category category = (com.luis.tcc.taskmanagerapi.entity.Category) result[0];
            Long count = (Long) result[1];
            if (category != null) {
                Map<String, Object> catInfo = new HashMap<>();
                catInfo.put("categoryName", category.getCategoryName());
                catInfo.put("count", count);
                mostUsedCategories.add(catInfo);
            }
        }
        analytics.put("mostUsedCategories", mostUsedCategories);

        return analytics;
    }
} 