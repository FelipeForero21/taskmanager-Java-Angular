package com.luis.tcc.taskmanagerapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String title;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;
    
    private LocalDateTime dueDate;
    
    @NotNull(message = "La prioridad es obligatoria")
    private Integer priorityId;
    
    @NotNull(message = "El estado es obligatorio")
    private Integer statusId;
    
    private Integer categoryId;
    
    private UUID assignedTo;
} 