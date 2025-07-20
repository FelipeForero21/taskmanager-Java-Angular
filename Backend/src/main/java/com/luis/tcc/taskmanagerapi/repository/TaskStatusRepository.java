package com.luis.tcc.taskmanagerapi.repository;

import com.luis.tcc.taskmanagerapi.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {
    
    // Obtener todos los estados activos ordenados por sortOrder
    List<TaskStatus> findByIsActiveTrueOrderBySortOrderAsc();
    
    // Buscar por nombre
    Optional<TaskStatus> findByStatusName(String statusName);
    
    // Buscar por nombre y activo
    Optional<TaskStatus> findByStatusNameAndIsActiveTrue(String statusName);
    
    // Verificar si existe por nombre
    boolean existsByStatusName(String statusName);
    
    // Obtener estados por orden de clasificación
    List<TaskStatus> findByIsActiveTrueOrderBySortOrderAscStatusNameAsc();
} 