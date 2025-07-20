package com.luis.tcc.taskmanagerapi.repository;

import com.luis.tcc.taskmanagerapi.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Integer> {
    
    // Obtener todas las prioridades activas ordenadas por nivel
    List<TaskPriority> findByIsActiveTrueOrderByPriorityLevelAsc();
    
    // Buscar por nombre
    Optional<TaskPriority> findByPriorityName(String priorityName);
    
    // Buscar por nombre y activo
    Optional<TaskPriority> findByPriorityNameAndIsActiveTrue(String priorityName);
    
    // Buscar por nivel de prioridad
    Optional<TaskPriority> findByPriorityLevelAndIsActiveTrue(Integer priorityLevel);
    
    // Verificar si existe por nombre
    boolean existsByPriorityName(String priorityName);
    
    // Obtener prioridades por nivel ascendente
    List<TaskPriority> findByIsActiveTrueOrderByPriorityLevelAscPriorityNameAsc();
} 