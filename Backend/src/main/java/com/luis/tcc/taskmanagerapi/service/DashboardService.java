package com.luis.tcc.taskmanagerapi.service;

import com.luis.tcc.taskmanagerapi.dto.DashboardResponse;
import com.luis.tcc.taskmanagerapi.entity.TaskPriority;
import com.luis.tcc.taskmanagerapi.entity.TaskStatus;
import com.luis.tcc.taskmanagerapi.repository.TaskPriorityRepository;
import com.luis.tcc.taskmanagerapi.repository.TaskRepository;
import com.luis.tcc.taskmanagerapi.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    
    public DashboardResponse getDashboardData(UUID userId) {
        // Obtener estadísticas básicas
        DashboardResponse.TaskStats taskStats = getTaskStats(userId);
        
        return DashboardResponse.builder()
                .taskStats(taskStats)
                .recentTasks(Collections.emptyList())
                .upcomingTasks(Collections.emptyList())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    
    private DashboardResponse.TaskStats getTaskStats(UUID userId) {
        // Conteos por estado
        long totalTasks = taskRepository.countTotalTasks(userId);
        long pendingTasks = taskRepository.countByStatus(userId, "Pendiente");
        long inProgressTasks = taskRepository.countByStatus(userId, "En Progreso");
        long completedTasks = taskRepository.countByStatus(userId, "Completada");
        
        long overdueTasksCount = 0;
        
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
    
} 