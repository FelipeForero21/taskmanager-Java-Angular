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
    
    //RUD 
    
    // Buscar tareas por usuario (creadas o asignadas)
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false")
    Page<Task> findByUserIdOrAssignedTo(@Param("userId") UUID userId, Pageable pageable);
    
    //  MÉTODOS DE BÚSQUEDA Y FILTRADO 
    
    // Búsqueda por texto en título o descripción
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> findBySearchTerm(@Param("userId") UUID userId, @Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Filtrar por estado
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND t.taskStatus = :status")
    Page<Task> findByStatus(@Param("userId") UUID userId, @Param("status") TaskStatus status, Pageable pageable);
    
    // Filtrar por prioridad
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND t.taskPriority = :priority")
    Page<Task> findByPriority(@Param("userId") UUID userId, @Param("priority") TaskPriority priority, Pageable pageable);
    
    // Filtrar por categoría
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND t.category.categoryId = :categoryId")
    Page<Task> findByCategory(@Param("userId") UUID userId, @Param("categoryId") Integer categoryId, Pageable pageable);
    
    // Filtrar por rango de fechas
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND " +
           "t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findByDueDateBetween(@Param("userId") UUID userId, 
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    
    // ===== MÉTODOS PARA DASHBOARD Y ESTADÍSTICAS =====
    // Tareas próximas a vencer (próximos 7 días)
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND " +
           "t.dueDate BETWEEN :now AND :nextWeek AND t.taskStatus.statusName != 'Completada'")
    List<Task> findUpcomingTasks(@Param("userId") UUID userId,
                                @Param("now") LocalDateTime now,
                                @Param("nextWeek") LocalDateTime nextWeek);
    
    // Tareas vencidas
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND " +
           "t.dueDate < :now AND t.taskStatus.statusName != 'Completada'")
    List<Task> findOverdueTasks(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    // Tareas más recientes
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false ORDER BY t.createdAt DESC")
    List<Task> findRecentTasks(@Param("userId") UUID userId, Pageable pageable);
    
    // CONTEO PARA ESTADÍSTICAS 
    
    // Contar tareas por prioridad
    @Query("SELECT COUNT(t) FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND t.taskPriority.priorityLevel = :priorityLevel")
    long countByPriorityLevel(@Param("userId") UUID userId, @Param("priorityLevel") Integer priorityLevel);
    
    // Contar tareas totales
    @Query("SELECT COUNT(t) FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false")
    long countTotalTasks(@Param("userId") UUID userId);
    
    // Contar tareas completadas en el último mes
    @Query("SELECT COUNT(t) FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false AND " +
           "t.taskStatus.statusName = 'Completada' AND t.completedAt >= :lastMonth")
    long countCompletedTasksLastMonth(@Param("userId") UUID userId, @Param("lastMonth") LocalDateTime lastMonth);
    
    // MÉTODOS ESPECIALES 
    
    // Buscar tareas por múltiples criterios
    @Query("SELECT t FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false " +
           "AND (:statusId IS NULL OR t.taskStatus.taskStatusId = :statusId) " +
           "AND (:priorityId IS NULL OR t.taskPriority.taskPriorityId = :priorityId) " +
           "AND (:categoryId IS NULL OR t.category.categoryId = :categoryId) " +
           "AND (:searchTerm IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> findByMultipleCriteria(@Param("userId") UUID userId,
                                     @Param("statusId") Integer statusId,
                                     @Param("priorityId") Integer priorityId,
                                     @Param("categoryId") Integer categoryId,
                                     @Param("searchTerm") String searchTerm,
                                     Pageable pageable);
    
    // Obtener horas estimadas vs reales
    @Query("SELECT COALESCE(SUM(t.estimatedHours), 0), COALESCE(SUM(t.actualHours), 0) FROM Task t " +
           "WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false")
    Object[] getHoursSummary(@Param("userId") UUID userId);

    // MÉTODOS DE CATEGORÍAS PARA DASHBOARD

    @Query("SELECT t.category, COUNT(t) FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false GROUP BY t.category")
    List<Object[]> getCategoriesWithTaskCount(@Param("userId") UUID userId);

    @Query("SELECT t.category, COUNT(t) as cnt FROM Task t WHERE (t.createdBy.userId = :userId OR t.assignedTo.userId = :userId) AND t.isDeleted = false GROUP BY t.category ORDER BY cnt DESC")
    List<Object[]> getMostUsedCategories(@Param("userId") UUID userId);

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