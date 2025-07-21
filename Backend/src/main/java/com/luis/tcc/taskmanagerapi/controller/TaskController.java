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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    
    //  MÉTODOS AUXILIARES 
    
    private UUID getUserIdFromRequest(HttpServletRequest request) {
        return userService.getCurrentUserId();
    }
} 