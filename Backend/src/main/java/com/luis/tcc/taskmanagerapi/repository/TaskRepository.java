package com.luis.tcc.taskmanagerapi.repository;

import com.luis.tcc.taskmanagerapi.entity.Task;
import com.luis.tcc.taskmanagerapi.entity.TaskPriority;
import com.luis.tcc.taskmanagerapi.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    // CONTEO PARA ESTADÍSTICAS 
    @Query("SELECT COUNT(t) FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND t.taskStatus.statusName = :statusName")
    long countByStatus(@Param("userId") UUID userId, @Param("statusName") String statusName);
    
    // Contar tareas totales
    @Query("SELECT COUNT(t) FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false")
    long countTotalTasks(@Param("userId") UUID userId);
    
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false " +
           "AND (:statusId IS NULL OR t.taskStatus.taskStatusId = :statusId) " +
           "AND (:priorityId IS NULL OR t.taskPriority.taskPriorityId = :priorityId) " +
           "AND (:categoryId IS NULL OR t.category.categoryId = :categoryId) " +
           "AND (:searchTerm IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:startDate IS NULL OR t.dueDate >= :startDate) " +
           "AND (:endDate IS NULL OR t.dueDate <= :endDate)")
    Page<Task> findByAllFilters(
        @Param("userId") UUID userId,
        @Param("statusId") Integer statusId,
        @Param("priorityId") Integer priorityId,
        @Param("categoryId") Integer categoryId,
        @Param("searchTerm") String searchTerm,
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate,
        org.springframework.data.domain.Pageable pageable
    );
} 