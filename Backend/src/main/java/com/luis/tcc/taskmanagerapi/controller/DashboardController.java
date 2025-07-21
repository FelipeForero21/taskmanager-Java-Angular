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
    
    private UUID getUserIdFromRequest(HttpServletRequest request) {
        return userService.getCurrentUserId();
    }
} 