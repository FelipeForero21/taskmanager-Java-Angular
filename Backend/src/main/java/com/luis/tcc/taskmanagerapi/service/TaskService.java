package com.luis.tcc.taskmanagerapi.service;

import com.luis.tcc.taskmanagerapi.dto.TaskRequest;
import com.luis.tcc.taskmanagerapi.dto.TaskResponse;
import com.luis.tcc.taskmanagerapi.entity.*;
import com.luis.tcc.taskmanagerapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskPriorityRepository taskPriorityRepository;
    private final CategoryRepository categoryRepository;
        
    @Transactional
    public TaskResponse createTask(TaskRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        TaskStatus status = taskStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        
        TaskPriority priority = taskPriorityRepository.findById(request.getPriorityId())
                .orElseThrow(() -> new RuntimeException("Prioridad no encontrada"));
        
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElse(null);
        }
        
        User assignedTo = user; 
        if (request.getAssignedTo() != null) {
            assignedTo = userRepository.findById(request.getAssignedTo())
                    .orElse(user);
        }
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setTaskStatus(status);
        task.setTaskPriority(priority);
        task.setCategory(category);
        task.setCreatedBy(user);
        task.setAssignedTo(assignedTo);
        task.setDueDate(request.getDueDate());
        task.setIsDeleted(false);
        
        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }
    
    public Page<TaskResponse> getUserTasks(UUID userId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByUserIdOrAssignedTo(userId, pageable);
        return tasks.map(this::convertToResponse);
    }
    
    public TaskResponse getTaskById(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        // Verificar que el usuario tenga acceso a la tarea
        if (!task.getCreatedBy().getUserId().equals(userId) && 
            !task.getAssignedTo().getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para ver esta tarea");
        }
        
        return convertToResponse(task);
    }
    
    @Transactional
    public TaskResponse updateTask(UUID taskId, TaskRequest request, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        // Verificar que el usuario tenga permisos para editar
        if (!task.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para editar esta tarea");
        }
        
        // Actualizar campos básicos
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        
        // Actualizar estado si cambió
        if (request.getStatusId() != null && !request.getStatusId().equals(task.getTaskStatus().getTaskStatusId())) {
            TaskStatus newStatus = taskStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
            task.setTaskStatus(newStatus);
        }
        
        // Actualizar prioridad si cambió
        if (request.getPriorityId() != null && !request.getPriorityId().equals(task.getTaskPriority().getTaskPriorityId())) {
            TaskPriority newPriority = taskPriorityRepository.findById(request.getPriorityId())
                    .orElseThrow(() -> new RuntimeException("Prioridad no encontrada"));
            task.setTaskPriority(newPriority);
        }
        
        // Actualizar categoría si cambió
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            task.setCategory(category);
        }
        
        // Actualizar asignación si cambió
        if (request.getAssignedTo() != null) {
            User assignedTo = userRepository.findById(request.getAssignedTo())
                    .orElse(task.getAssignedTo());
            task.setAssignedTo(assignedTo);
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }
    
    @Transactional
    public void deleteTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        // Verificar que el usuario tenga permisos para eliminar
        if (!task.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para eliminar esta tarea");
        }
        
        // Soft delete
        task.setIsDeleted(true);
        taskRepository.save(task);
    }
    
    //MÉTODOS DE BÚSQUEDA Y FILTRADO
    public Page<TaskResponse> searchTasks(UUID userId, String searchTerm, Pageable pageable) {
        Page<Task> tasks = taskRepository.findBySearchTerm(userId, searchTerm, pageable);
        return tasks.map(this::convertToResponse);
    }
    
    public Page<TaskResponse> filterTasksByStatus(UUID userId, String statusName, Pageable pageable) {
        TaskStatus status = taskStatusRepository.findByStatusName(statusName)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        
        Page<Task> tasks = taskRepository.findByStatus(userId, status, pageable);
        return tasks.map(this::convertToResponse);
    }
    
    public Page<TaskResponse> filterTasksByPriority(UUID userId, String priorityName, Pageable pageable) {
        TaskPriority priority = taskPriorityRepository.findByPriorityName(priorityName)
                .orElseThrow(() -> new RuntimeException("Prioridad no encontrada"));
        
        Page<Task> tasks = taskRepository.findByPriority(userId, priority, pageable);
        return tasks.map(this::convertToResponse);
    }
    
    public Page<TaskResponse> filterTasksByCategory(UUID userId, Integer categoryId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByCategory(userId, categoryId, pageable);
        return tasks.map(this::convertToResponse);
    }
    
    public List<TaskResponse> filterTasksByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Task> tasks = taskRepository.findByDueDateBetween(userId, startDate, endDate);
        return tasks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public Page<TaskResponse> filterTasksByMultipleCriteria(UUID userId, Integer statusId, Integer priorityId, 
                                                           Integer categoryId, String searchTerm, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByMultipleCriteria(userId, statusId, priorityId, categoryId, searchTerm, pageable);
        return tasks.map(this::convertToResponse);
    }
    
    //DASHBOARD
    
    public List<TaskResponse> getUpcomingTasks(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);
        
        List<Task> tasks = taskRepository.findUpcomingTasks(userId, now, nextWeek);
        return tasks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<TaskResponse> getOverdueTasks(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findOverdueTasks(userId, now);
        return tasks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<TaskResponse> getRecentTasks(UUID userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Task> tasks = taskRepository.findRecentTasks(userId, pageable);
        return tasks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    //MÉTODOS DE ASIGNACIÓN
    
    @Transactional
    public TaskResponse assignTask(UUID taskId, UUID assigneeId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        // Verificar que el usuario tenga permisos para asignar
        if (!task.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para asignar esta tarea");
        }
        
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Usuario asignado no encontrado"));
        
        task.setAssignedTo(assignee);
        Task updatedTask = taskRepository.save(task);
        
        return convertToResponse(updatedTask);
    }
    
    //MÉTODOS DE ESTADÍSTICAS
    
    public long getTaskCountByStatus(UUID userId, String statusName) {
        return taskRepository.countByStatus(userId, statusName);
    }
    
    public long getTaskCountByPriority(UUID userId, Integer priorityLevel) {
        return taskRepository.countByPriorityLevel(userId, priorityLevel);
    }
    
    public long getTotalTasks(UUID userId) {
        return taskRepository.countTotalTasks(userId);
    }
    
    public long getCompletedTasksLastMonth(UUID userId) {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        return taskRepository.countCompletedTasksLastMonth(userId, lastMonth);
    }
    
    public Object[] getHoursSummary(UUID userId) {
        return taskRepository.getHoursSummary(userId);
    }
    
    //MÉTODOS AUXILIARES
    
    private TaskResponse convertToResponse(Task task) {
        // Crear información de prioridad
        TaskResponse.PriorityInfo priorityInfo = TaskResponse.PriorityInfo.builder()
                .priorityId(task.getTaskPriority().getTaskPriorityId())
                .priorityName(task.getTaskPriority().getPriorityName())
                .priorityLevel(task.getTaskPriority().getPriorityLevel())
                .colorHex(task.getTaskPriority().getColorHex())
                .build();
        
        // Crear información de estado
        TaskResponse.StatusInfo statusInfo = TaskResponse.StatusInfo.builder()
                .statusId(task.getTaskStatus().getTaskStatusId())
                .statusName(task.getTaskStatus().getStatusName())
                .colorHex(task.getTaskStatus().getColorHex())
                .build();
        
        // Crear información de categoría
        TaskResponse.CategoryInfo categoryInfo = null;
        if (task.getCategory() != null) {
            categoryInfo = TaskResponse.CategoryInfo.builder()
                    .categoryId(task.getCategory().getCategoryId())
                    .categoryName(task.getCategory().getCategoryName())
                    .colorHex(task.getCategory().getColorHex())
                    .iconName(task.getCategory().getIconName())
                    .build();
        }
        
        // Crear información del usuario creador
        TaskResponse.UserInfo createdByInfo = TaskResponse.UserInfo.builder()
                .userId(task.getCreatedBy().getUserId())
                .email(task.getCreatedBy().getEmail())
                .firstName(task.getCreatedBy().getFirstName())
                .lastName(task.getCreatedBy().getLastName())
                .build();
        
        // Crear información del usuario asignado
        TaskResponse.UserInfo assignedToInfo = null;
        if (task.getAssignedTo() != null) {
            assignedToInfo = TaskResponse.UserInfo.builder()
                    .userId(task.getAssignedTo().getUserId())
                    .email(task.getAssignedTo().getEmail())
                    .firstName(task.getAssignedTo().getFirstName())
                    .lastName(task.getAssignedTo().getLastName())
                    .build();
        }
        
        // Calcular si está vencida y días hasta vencimiento
        boolean isOverdue = false;
        long daysUntilDue = 0;
        if (task.getDueDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            isOverdue = task.getDueDate().isBefore(now) && !"Completada".equals(task.getTaskStatus().getStatusName());
            daysUntilDue = java.time.Duration.between(now, task.getDueDate()).toDays();
        }
        
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .isActive(!task.getIsDeleted())
                .priority(priorityInfo)
                .status(statusInfo)
                .category(categoryInfo)
                .assignedTo(assignedToInfo)
                .createdBy(createdByInfo)
                .isOverdue(isOverdue)
                .daysUntilDue(daysUntilDue)
                .build();
    }
} 