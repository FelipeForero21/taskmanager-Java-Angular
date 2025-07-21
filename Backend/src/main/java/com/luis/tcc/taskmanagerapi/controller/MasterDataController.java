package com.luis.tcc.taskmanagerapi.controller;

import com.luis.tcc.taskmanagerapi.entity.TaskPriority;
import com.luis.tcc.taskmanagerapi.entity.TaskStatus;
import com.luis.tcc.taskmanagerapi.service.MasterDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/master-data")
@RequiredArgsConstructor
@Tag(name = "Datos Maestros", description = "APIs para datos maestros del sistema")
@CrossOrigin(origins = {"https://taskmanager.tcc.com.co", "https://www.taskmanager.tcc.com.co"})
public class MasterDataController {
    
    private final MasterDataService masterDataService;
    
    //  MÉTODOS PARA ESTADOS
    
    @GetMapping("/statuses")
    @Operation(summary = "Obtener estados", description = "Obtiene todos los estados de tareas activos")
    public ResponseEntity<List<TaskStatus>> getAllStatuses() {
        try {
            List<TaskStatus> statuses = masterDataService.getAllActiveStatuses();
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estados: " + e.getMessage());
        }
    }
    
    //  MÉTODOS PARA PRIORIDADES 
    
    @GetMapping("/priorities")
    @Operation(summary = "Obtener prioridades", description = "Obtiene todas las prioridades de tareas activas")
    public ResponseEntity<List<TaskPriority>> getAllPriorities() {
        try {
            List<TaskPriority> priorities = masterDataService.getAllActivePriorities();
            return ResponseEntity.ok(priorities);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener prioridades: " + e.getMessage());
        }
    }
    
} 