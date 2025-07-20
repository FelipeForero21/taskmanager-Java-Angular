package com.luis.tcc.taskmanagerapi.service;

import com.luis.tcc.taskmanagerapi.entity.Category;
import com.luis.tcc.taskmanagerapi.entity.User;
import com.luis.tcc.taskmanagerapi.repository.CategoryRepository;
import com.luis.tcc.taskmanagerapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    
    //CRUD BÁSICOS 
    
    @Transactional
    public Category createCategory(String categoryName, String description, String colorHex, String iconName, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar si ya existe una categoría con el mismo nombre para este usuario
        if (categoryRepository.existsByCategoryNameAndCreatedByUserId(categoryName, userId)) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + categoryName);
        }
        
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setCategoryDescription(description);
        category.setColorHex(colorHex != null ? colorHex : "#007bff");
        category.setIconName(iconName);
        category.setCreatedBy(user);
        category.setIsActive(true);
        
        return categoryRepository.save(category);
    }
    
    public List<Category> getUserCategories(UUID userId) {
        return categoryRepository.findByCreatedByUserIdAndIsActiveTrueOrderByCategoryNameAsc(userId);
    }
    
    public Optional<Category> getCategoryById(Integer categoryId, UUID userId) {
        return categoryRepository.findById(categoryId)
                .filter(category -> category.getCreatedBy().getUserId().equals(userId) && category.getIsActive());
    }
    
    @Transactional
    public Category updateCategory(Integer categoryId, String categoryName, String description, 
                                 String colorHex, String iconName, UUID userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        // Verificar que el usuario sea el propietario
        if (!category.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para editar esta categoría");
        }
        
        // Verificar si el nuevo nombre ya existe (si cambió)
        if (!category.getCategoryName().equals(categoryName) && 
            categoryRepository.existsByCategoryNameAndCreatedByUserId(categoryName, userId)) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + categoryName);
        }
        
        category.setCategoryName(categoryName);
        category.setCategoryDescription(description);
        category.setColorHex(colorHex != null ? colorHex : category.getColorHex());
        category.setIconName(iconName);
        
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void deleteCategory(Integer categoryId, UUID userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        
        // Verificar que el usuario sea el propietario
        if (!category.getCreatedBy().getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para eliminar esta categoría");
        }
        
        // Soft delete
        category.setIsActive(false);
        categoryRepository.save(category);
    }
    
    // MÉTODOS DE BÚSQUEDA 
    
    public List<Category> searchCategories(UUID userId, String searchTerm) {
        return categoryRepository.findBySearchTerm(userId, searchTerm);
    }
    
    public Optional<Category> getCategoryByName(String categoryName, UUID userId) {
        return categoryRepository.findByCategoryNameAndCreatedByUserIdAndIsActiveTrue(categoryName, userId);
    }
    
    //  MÉTODOS DE ANÁLISIS 
    
    public List<Object[]> getCategoriesWithTaskCount(UUID userId) {
        return categoryRepository.findCategoriesWithTaskCount(userId);
    }
    
    public List<Object[]> getMostUsedCategories(UUID userId) {
        return categoryRepository.findMostUsedCategories(userId);
    }
    
    // MÉTODOS AUXILIARES 
    
    public boolean categoryExists(String categoryName, UUID userId) {
        return categoryRepository.existsByCategoryNameAndCreatedByUserId(categoryName, userId);
    }
    
    public boolean categoryExistsById(Integer categoryId, UUID userId) {
        return categoryRepository.findById(categoryId)
                .map(category -> category.getCreatedBy().getUserId().equals(userId) && category.getIsActive())
                .orElse(false);
    }
    
    //  MÉTODOS PARA CATEGORÍAS POR DEFECTO 
    
    @Transactional
    public void createDefaultCategories(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Crear categorías por defecto si no existen
        createDefaultCategoryIfNotExists("Trabajo", "Tareas relacionadas con el trabajo", "#007bff", "work", user);
        createDefaultCategoryIfNotExists("Personal", "Tareas personales", "#28a745", "person", user);
        createDefaultCategoryIfNotExists("Urgente", "Tareas que requieren atención inmediata", "#dc3545", "priority_high", user);
        createDefaultCategoryIfNotExists("Proyectos", "Tareas de proyectos específicos", "#ffc107", "folder", user);
        createDefaultCategoryIfNotExists("Reuniones", "Tareas relacionadas con reuniones", "#6f42c1", "event", user);
    }
    
    private void createDefaultCategoryIfNotExists(String name, String description, String color, String icon, User user) {
        if (!categoryRepository.existsByCategoryNameAndCreatedByUserId(name, user.getUserId())) {
            Category category = new Category();
            category.setCategoryName(name);
            category.setCategoryDescription(description);
            category.setColorHex(color);
            category.setIconName(icon);
            category.setCreatedBy(user);
            category.setIsActive(true);
            categoryRepository.save(category);
        }
    }
    
    // MÉTODOS PARA ESTADÍSTICAS DE CATEGORÍAS
    
    public Map<String, Object> getCategoryStatistics(UUID userId) {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // Total de categorías
        List<Category> categories = getUserCategories(userId);
        stats.put("totalCategories", categories.size());
        
        // Categorías con tareas
        List<Object[]> categoriesWithCount = getCategoriesWithTaskCount(userId);
        stats.put("categoriesWithTasks", categoriesWithCount.size());
        
        // Categoría más usada
        List<Object[]> mostUsed = getMostUsedCategories(userId);
        if (!mostUsed.isEmpty()) {
            Object[] mostUsedCategory = mostUsed.get(0);
            Category category = (Category) mostUsedCategory[0];
            Long count = (Long) mostUsedCategory[1];
            stats.put("mostUsedCategory", category.getCategoryName());
            stats.put("mostUsedCategoryCount", count);
        }
        
        return stats;
    }
} 