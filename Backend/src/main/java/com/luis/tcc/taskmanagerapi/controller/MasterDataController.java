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
    
    @GetMapping("/statuses/{statusName}")
    @Operation(summary = "Obtener estado por nombre", description = "Obtiene un estado específico por nombre")
    public ResponseEntity<TaskStatus> getStatusByName(
            @Parameter(description = "Nombre del estado") @PathVariable String statusName) {
        try {
            return masterDataService.getStatusByName(statusName)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estado por nombre: " + e.getMessage());
        }
    }
    
    @GetMapping("/statuses/id/{statusId}")
    @Operation(summary = "Obtener estado por ID", description = "Obtiene un estado específico por ID")
    public ResponseEntity<TaskStatus> getStatusById(
            @Parameter(description = "ID del estado") @PathVariable Integer statusId) {
        try {
            return masterDataService.getStatusById(statusId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estado por ID: " + e.getMessage());
        }
    }
    
    @GetMapping("/statuses/exists/{statusName}")
    @Operation(summary = "Verificar existencia de estado", description = "Verifica si existe un estado con el nombre especificado")
    public ResponseEntity<Map<String, Boolean>> statusExists(
            @Parameter(description = "Nombre del estado") @PathVariable String statusName) {
        try {
            boolean exists = masterDataService.statusExists(statusName);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de estado: " + e.getMessage());
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
    
    @GetMapping("/priorities/{priorityName}")
    @Operation(summary = "Obtener prioridad por nombre", description = "Obtiene una prioridad específica por nombre")
    public ResponseEntity<TaskPriority> getPriorityByName(
            @Parameter(description = "Nombre de la prioridad") @PathVariable String priorityName) {
        try {
            return masterDataService.getPriorityByName(priorityName)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener prioridad por nombre: " + e.getMessage());
        }
    }
    
    @GetMapping("/priorities/level/{priorityLevel}")
    @Operation(summary = "Obtener prioridad por nivel", description = "Obtiene una prioridad específica por nivel")
    public ResponseEntity<TaskPriority> getPriorityByLevel(
            @Parameter(description = "Nivel de prioridad") @PathVariable Integer priorityLevel) {
        try {
            return masterDataService.getPriorityByLevel(priorityLevel)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener prioridad por nivel: " + e.getMessage());
        }
    }
    
    @GetMapping("/priorities/id/{priorityId}")
    @Operation(summary = "Obtener prioridad por ID", description = "Obtiene una prioridad específica por ID")
    public ResponseEntity<TaskPriority> getPriorityById(
            @Parameter(description = "ID de la prioridad") @PathVariable Integer priorityId) {
        try {
            return masterDataService.getPriorityById(priorityId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener prioridad por ID: " + e.getMessage());
        }
    }
    
    @GetMapping("/priorities/exists/{priorityName}")
    @Operation(summary = "Verificar existencia de prioridad", description = "Verifica si existe una prioridad con el nombre especificado")
    public ResponseEntity<Map<String, Boolean>> priorityExists(
            @Parameter(description = "Nombre de la prioridad") @PathVariable String priorityName) {
        try {
            boolean exists = masterDataService.priorityExists(priorityName);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de prioridad: " + e.getMessage());
        }
    }
    
    //  MÉTODOS PARA DATOS MAESTROS COMPLETOS 
    
    @GetMapping("/all")
    @Operation(summary = "Obtener todos los datos maestros", description = "Obtiene todos los estados y prioridades en una sola respuesta")
    public ResponseEntity<MasterDataService.MasterDataResponse> getAllMasterData() {
        try {
            MasterDataService.MasterDataResponse response = masterDataService.getAllMasterData();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener datos maestros: " + e.getMessage());
        }
    }
    
    @GetMapping("/statuses/summary")
    @Operation(summary = "Resumen de estados", description = "Obtiene un resumen de los estados disponibles")
    public ResponseEntity<Map<String, Object>> getStatusesSummary() {
        try {
            List<TaskStatus> statuses = masterDataService.getAllActiveStatuses();
            
            Map<String, Object> summary = Map.of(
                "totalStatuses", statuses.size(),
                "statuses", statuses,
                "defaultStatus", "Pendiente"
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener resumen de estados: " + e.getMessage());
        }
    }
    
    @GetMapping("/priorities/summary")
    @Operation(summary = "Resumen de prioridades", description = "Obtiene un resumen de las prioridades disponibles")
    public ResponseEntity<Map<String, Object>> getPrioritiesSummary() {
        try {
            List<TaskPriority> priorities = masterDataService.getAllActivePriorities();
            
            Map<String, Object> summary = Map.of(
                "totalPriorities", priorities.size(),
                "priorities", priorities,
                "defaultPriority", "Media"
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener resumen de prioridades: " + e.getMessage());
        }
    }
    
    @GetMapping("/validation/status/{statusName}")
    @Operation(summary = "Validar estado", description = "Valida si un estado es válido para el sistema")
    public ResponseEntity<Map<String, Object>> validateStatus(
            @Parameter(description = "Nombre del estado") @PathVariable String statusName) {
        try {
            boolean exists = masterDataService.statusExists(statusName);
            boolean isValid = exists;
            
            Map<String, Object> validation = Map.of(
                "statusName", statusName,
                "exists", exists,
                "isValid", isValid,
                "message", isValid ? "Estado válido" : "Estado no válido"
            );
            
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            throw new RuntimeException("Error al validar estado: " + e.getMessage());
        }
    }
    
    @GetMapping("/validation/priority/{priorityName}")
    @Operation(summary = "Validar prioridad", description = "Valida si una prioridad es válida para el sistema")
    public ResponseEntity<Map<String, Object>> validatePriority(
            @Parameter(description = "Nombre de la prioridad") @PathVariable String priorityName) {
        try {
            boolean exists = masterDataService.priorityExists(priorityName);
            boolean isValid = exists;
            
            Map<String, Object> validation = Map.of(
                "priorityName", priorityName,
                "exists", exists,
                "isValid", isValid,
                "message", isValid ? "Prioridad válida" : "Prioridad no válida"
            );
            
            return ResponseEntity.ok(validation);
        } catch (Exception e) {
            throw new RuntimeException("Error al validar prioridad: " + e.getMessage());
        }
    }
} 