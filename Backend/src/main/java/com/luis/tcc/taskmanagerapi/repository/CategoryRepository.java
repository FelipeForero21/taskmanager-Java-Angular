package com.luis.tcc.taskmanagerapi.repository;

import com.luis.tcc.taskmanagerapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    // Obtener categorías activas de un usuario
    List<Category> findByCreatedByUserIdAndIsActiveTrueOrderByCategoryNameAsc(UUID userId);
    
    // Buscar categoría por nombre y usuario
    Optional<Category> findByCategoryNameAndCreatedByUserId(String categoryName, UUID userId);
    
    // Buscar categoría por nombre, usuario y activa
    Optional<Category> findByCategoryNameAndCreatedByUserIdAndIsActiveTrue(String categoryName, UUID userId);
    
    // Verificar si existe categoría por nombre y usuario
    boolean existsByCategoryNameAndCreatedByUserId(String categoryName, UUID userId);
    
    // Buscar categorías por término de búsqueda
    @Query("SELECT c FROM Category c WHERE c.createdBy.userId = :userId AND c.isActive = true AND " +
           "LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Category> findBySearchTerm(@Param("userId") UUID userId, @Param("searchTerm") String searchTerm);
    
    // Obtener categorías con conteo de tareas
    @Query("SELECT c, COUNT(t) as taskCount FROM Category c LEFT JOIN Task t ON c.categoryId = t.category.categoryId " +
           "WHERE c.createdBy.userId = :userId AND c.isActive = true AND (t.isDeleted = false OR t.isDeleted IS NULL) " +
           "GROUP BY c ORDER BY c.categoryName ASC")
    List<Object[]> findCategoriesWithTaskCount(@Param("userId") UUID userId);
    
    // Obtener categorías más usadas
    @Query("SELECT c, COUNT(t) as taskCount FROM Category c LEFT JOIN Task t ON c.categoryId = t.category.categoryId " +
           "WHERE c.createdBy.userId = :userId AND c.isActive = true AND t.isDeleted = false " +
           "GROUP BY c ORDER BY taskCount DESC, c.categoryName ASC")
    List<Object[]> findMostUsedCategories(@Param("userId") UUID userId);
} 