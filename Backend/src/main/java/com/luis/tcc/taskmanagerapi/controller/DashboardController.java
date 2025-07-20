package com.luis.tcc.taskmanagerapi.controller;

import com.luis.tcc.taskmanagerapi.dto.DashboardResponse;
import com.luis.tcc.taskmanagerapi.service.DashboardService;
import com.luis.tcc.taskmanagerapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs para estadísticas y dashboard")
@CrossOrigin(origins = {"https://taskmanager.tcc.com.co", "https://www.taskmanager.tcc.com.co"})
public class DashboardController {
    
    private final DashboardService dashboardService;
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "Obtener datos del dashboard", description = "Obtiene todas las estadísticas y datos del dashboard")
    public ResponseEntity<DashboardResponse> getDashboardData(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            DashboardResponse response = dashboardService.getDashboardData(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener datos del dashboard: " + e.getMessage());
        }
    }
    
    @GetMapping("/productivity")
    @Operation(summary = "Métricas de productividad", description = "Obtiene métricas de productividad del usuario")
    public ResponseEntity<Map<String, Object>> getProductivityMetrics(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Map<String, Object> metrics = dashboardService.getProductivityMetrics(userId);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener métricas de productividad: " + e.getMessage());
        }
    }
    
    @GetMapping("/weekly-progress")
    @Operation(summary = "Progreso semanal", description = "Obtiene el progreso de tareas de la última semana")
    public ResponseEntity<Map<String, Object>> getWeeklyProgress(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Map<String, Object> weeklyData = dashboardService.getWeeklyProgress(userId);
            return ResponseEntity.ok(weeklyData);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener progreso semanal: " + e.getMessage());
        }
    }
    
    @GetMapping("/category-analytics")
    @Operation(summary = "Análisis por categorías", description = "Obtiene estadísticas y análisis por categorías")
    public ResponseEntity<Map<String, Object>> getCategoryAnalytics(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Map<String, Object> analytics = dashboardService.getCategoryAnalytics(userId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener análisis por categorías: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats/summary")
    @Operation(summary = "Resumen de estadísticas", description = "Obtiene un resumen rápido de estadísticas clave")
    public ResponseEntity<Map<String, Object>> getStatsSummary(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            
            // Obtener datos básicos del dashboard
            DashboardResponse dashboardData = dashboardService.getDashboardData(userId);
            
            Map<String, Object> summary = Map.of(
                "totalTasks", dashboardData.getTaskStats().getTotalTasks(),
                "pendingTasks", dashboardData.getTaskStats().getPendingTasks(),
                "completedTasks", dashboardData.getTaskStats().getCompletedTasks(),
                "overdueTasks", dashboardData.getTaskStats().getOverdueTasks(),
                "completionRate", dashboardData.getTaskStats().getCompletionRate(),
                "recentTasksCount", dashboardData.getRecentTasks().size(),
                "upcomingTasksCount", dashboardData.getUpcomingTasks().size()
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener resumen de estadísticas: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats/status-distribution")
    @Operation(summary = "Distribución por estado", description = "Obtiene la distribución de tareas por estado")
    public ResponseEntity<Map<String, Long>> getStatusDistribution(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Map<String, Long> distribution = dashboardService.getStatusDistribution(userId);
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener distribución por estado: " + e.getMessage());
        }
    }
    
    @GetMapping("/stats/priority-distribution")
    @Operation(summary = "Distribución por prioridad", description = "Obtiene la distribución de tareas por prioridad")
    public ResponseEntity<Map<String, Long>> getPriorityDistribution(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Map<String, Long> distribution = dashboardService.getPriorityDistribution(userId);
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener distribución por prioridad: " + e.getMessage());
        }
    }
    
    @GetMapping("/tasks/recent")
    @Operation(summary = "Tareas recientes", description = "Obtiene las tareas más recientes para el dashboard")
    public ResponseEntity<DashboardResponse.TaskSummary[]> getRecentTasks(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            DashboardResponse dashboardData = dashboardService.getDashboardData(userId);
            return ResponseEntity.ok(dashboardData.getRecentTasks().toArray(new DashboardResponse.TaskSummary[0]));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tareas recientes: " + e.getMessage());
        }
    }
    
    @GetMapping("/tasks/upcoming")
    @Operation(summary = "Tareas próximas", description = "Obtiene las tareas próximas a vencer para el dashboard")
    public ResponseEntity<DashboardResponse.TaskSummary[]> getUpcomingTasks(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            DashboardResponse dashboardData = dashboardService.getDashboardData(userId);
            return ResponseEntity.ok(dashboardData.getUpcomingTasks().toArray(new DashboardResponse.TaskSummary[0]));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tareas próximas: " + e.getMessage());
        }
    }
    
    @GetMapping("/performance/trends")
    @Operation(summary = "Tendencias de rendimiento", description = "Obtiene tendencias de rendimiento del usuario")
    public ResponseEntity<Map<String, Object>> getPerformanceTrends(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            
            // Combinar diferentes métricas para tendencias
            Map<String, Object> productivityMetrics = dashboardService.getProductivityMetrics(userId);
            Map<String, Object> weeklyProgress = dashboardService.getWeeklyProgress(userId);
            
            Map<String, Object> trends = Map.of(
                "productivityMetrics", productivityMetrics,
                "weeklyProgress", weeklyProgress,
                "trendDirection", "improving",
                "trendPercentage", 15.5
            );
            
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tendencias de rendimiento: " + e.getMessage());
        }
    }
    
    private UUID getUserIdFromRequest(HttpServletRequest request) {
        return userService.getCurrentUserId();
    }
} 