package com.luis.tcc.taskmanagerapi.controller;

import com.luis.tcc.taskmanagerapi.dto.TaskRequest;
import com.luis.tcc.taskmanagerapi.dto.TaskResponse;
import com.luis.tcc.taskmanagerapi.service.TaskService;
import com.luis.tcc.taskmanagerapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "APIs para gestión de tareas")
@CrossOrigin(origins = {"https://taskmanager.tcc.com.co", "https://www.taskmanager.tcc.com.co"})
public class TaskController {
    
    private final TaskService taskService;
    private final UserService userService;
    
    // CRUD 
    
    @PostMapping
    @Operation(summary = "Crear tarea", description = "Crea una nueva tarea")

    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            TaskResponse response = taskService.createTask(request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear tarea: " + e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "Obtener tareas", description = "Obtiene la lista paginada de tareas del usuario con filtros avanzados")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) Integer priorityId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<TaskResponse> tasks = taskService.filterTasksAdvanced(
                userId, statusId, priorityId, categoryId, searchTerm, startDate, endDate, pageable
            );
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tareas: " + e.getMessage());
        }
    }
    
    @GetMapping("/{taskId}")
    @Operation(summary = "Obtener tarea por ID", description = "Obtiene los detalles de una tarea específica")
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            TaskResponse response = taskService.getTaskById(taskId, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tarea: " + e.getMessage());
        }
    }
    
    @PutMapping("/{taskId}")
    @Operation(summary = "Actualizar tarea", description = "Actualiza una tarea existente")
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            @Valid @RequestBody TaskRequest request,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            TaskResponse response = taskService.updateTask(taskId, request, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar tarea: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{taskId}")
    @Operation(summary = "Eliminar tarea", description = "Elimina una tarea (soft delete)")
    public ResponseEntity<Map<String, String>> deleteTask(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            taskService.deleteTask(taskId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Tarea eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar tarea: " + e.getMessage());
        }
    }
    
    // MÉTODOS DE BÚSQUEDA Y FILTRADO 
    
    @GetMapping("/search")
    @Operation(summary = "Buscar tareas", description = "Busca tareas por texto en título o descripción")
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            @Parameter(description = "Término de búsqueda") @RequestParam String searchTerm,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskResponse> tasks = taskService.searchTasks(userId, searchTerm, pageable);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error en la búsqueda: " + e.getMessage());
        }
    }
    
    @GetMapping("/filter/status/{statusName}")
    @Operation(summary = "Filtrar por estado", description = "Filtra tareas por estado")
    public ResponseEntity<Page<TaskResponse>> filterByStatus(
            @Parameter(description = "Nombre del estado") @PathVariable String statusName,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskResponse> tasks = taskService.filterTasksByStatus(userId, statusName, pageable);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error al filtrar por estado: " + e.getMessage());
        }
    }
    
    @GetMapping("/filter/priority/{priorityName}")
    @Operation(summary = "Filtrar por prioridad", description = "Filtra tareas por prioridad")
    public ResponseEntity<Page<TaskResponse>> filterByPriority(
            @Parameter(description = "Nombre de la prioridad") @PathVariable String priorityName,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskResponse> tasks = taskService.filterTasksByPriority(userId, priorityName, pageable);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error al filtrar por prioridad: " + e.getMessage());
        }
    }
    
    @GetMapping("/filter/category/{categoryId}")
    @Operation(summary = "Filtrar por categoría", description = "Filtra tareas por categoría")
    public ResponseEntity<Page<TaskResponse>> filterByCategory(
            @Parameter(description = "ID de la categoría") @PathVariable Integer categoryId,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskResponse> tasks = taskService.filterTasksByCategory(userId, categoryId, pageable);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error al filtrar por categoría: " + e.getMessage());
        }
    }
    
    @GetMapping("/filter/date-range")
    @Operation(summary = "Filtrar por rango de fechas", description = "Filtra tareas por rango de fechas de vencimiento")
    public ResponseEntity<List<TaskResponse>> filterByDateRange(
            @Parameter(description = "Fecha de inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            List<TaskResponse> tasks = taskService.filterTasksByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error al filtrar por rango de fechas: " + e.getMessage());
        }
    }
    
    @GetMapping("/filter/multiple")
    @Operation(summary = "Filtro múltiple", description = "Filtra tareas por múltiples criterios")
    public ResponseEntity<Page<TaskResponse>> filterByMultipleCriteria(
            @Parameter(description = "ID del estado") @RequestParam(required = false) Integer statusId,
            @Parameter(description = "ID de la prioridad") @RequestParam(required = false) Integer priorityId,
            @Parameter(description = "ID de la categoría") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "Término de búsqueda") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Pageable pageable = PageRequest.of(page, size);
            Page<TaskResponse> tasks = taskService.filterTasksByMultipleCriteria(
                    userId, statusId, priorityId, categoryId, searchTerm, pageable);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error en filtro múltiple: " + e.getMessage());
        }
    }
    
    // MÉTODOS PARA DASHBOARD 
    
    @GetMapping("/upcoming")
    @Operation(summary = "Tareas próximas", description = "Obtiene tareas que vencen en los próximos 7 días")
    public ResponseEntity<List<TaskResponse>> getUpcomingTasks(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            List<TaskResponse> tasks = taskService.getUpcomingTasks(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tareas próximas: " + e.getMessage());
        }
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Tareas vencidas", description = "Obtiene tareas que han vencido")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            List<TaskResponse> tasks = taskService.getOverdueTasks(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tareas vencidas: " + e.getMessage());
        }
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Tareas recientes", description = "Obtiene las tareas más recientes")
    public ResponseEntity<List<TaskResponse>> getRecentTasks(
            @Parameter(description = "Límite de tareas") @RequestParam(defaultValue = "5") int limit,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            List<TaskResponse> tasks = taskService.getRecentTasks(userId, limit);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tareas recientes: " + e.getMessage());
        }
    }
    
    //  MÉTODOS DE ASIGNACIÓN 
    
    @PutMapping("/{taskId}/assign")
    @Operation(summary = "Asignar tarea", description = "Asigna una tarea a otro usuario")
    public ResponseEntity<TaskResponse> assignTask(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            @Parameter(description = "ID del usuario asignado") @RequestParam UUID assigneeId,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            TaskResponse response = taskService.assignTask(taskId, assigneeId, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al asignar tarea: " + e.getMessage());
        }
    }
    
    //  MÉTODOS AUXILIARES 
    
    private UUID getUserIdFromRequest(HttpServletRequest request) {
        return userService.getCurrentUserId();
    }
} 