package com.luis.tcc.taskmanagerapi.controller;

import com.luis.tcc.taskmanagerapi.entity.Category;
import com.luis.tcc.taskmanagerapi.service.CategoryService;
import com.luis.tcc.taskmanagerapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "APIs para gestión de categorías")
@CrossOrigin(origins = {"https://taskmanager.tcc.com.co", "https://www.taskmanager.tcc.com.co"})
public class CategoryController {
    
    private final CategoryService categoryService;
    private final UserService userService;
    
    //  MÉTODOS CRUD BÁSICOS 
    
    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría para el usuario")

    public ResponseEntity<Category> createCategory(
        @Parameter(description = "Nombre de la categoría") @RequestParam String categoryName,
        @Parameter(description = "Descripción de la categoría") @RequestParam(required = false) String description,
        @Parameter(description = "Color hexadecimal") @RequestParam(required = false) String colorHex,
        @Parameter(description = "Nombre del icono") @RequestParam(required = false) String iconName,

            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Category category = categoryService.createCategory(categoryName, description, colorHex, iconName, userId);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear categoría: " + e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "Obtener categorías", description = "Obtiene todas las categorías del usuario")
    public ResponseEntity<List<Category>> getUserCategories(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            List<Category> categories = categoryService.getUserCategories(userId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categorías: " + e.getMessage());
        }
    }
    
    @GetMapping("/{categoryId}")
    @Operation(summary = "Obtener categoría por ID", description = "Obtiene una categoría específica por ID")
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "ID de la categoría") @PathVariable Integer categoryId,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            return categoryService.getCategoryById(categoryId, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categoría: " + e.getMessage());
        }
    }
    
    @PutMapping("/{categoryId}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente")
    public ResponseEntity<Category> updateCategory(
            @Parameter(description = "ID de la categoría") @PathVariable Integer categoryId,
            @Parameter(description = "Nombre de la categoría") @RequestParam String categoryName,
            @Parameter(description = "Descripción de la categoría") @RequestParam(required = false) String description,
            @Parameter(description = "Color hexadecimal") @RequestParam(required = false) String colorHex,
            @Parameter(description = "Nombre del icono") @RequestParam(required = false) String iconName,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Category category = categoryService.updateCategory(categoryId, categoryName, description, colorHex, iconName, userId);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar categoría: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría (soft delete)")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @Parameter(description = "ID de la categoría") @PathVariable Integer categoryId,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            categoryService.deleteCategory(categoryId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Categoría eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar categoría: " + e.getMessage());
        }
    }
    
    //  MÉTODOS DE BÚSQUEDA 
    
    @GetMapping("/search")
    @Operation(summary = "Buscar categorías", description = "Busca categorías por término")
    public ResponseEntity<List<Category>> searchCategories(
            @Parameter(description = "Término de búsqueda") @RequestParam String searchTerm,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            List<Category> categories = categoryService.searchCategories(userId, searchTerm);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            throw new RuntimeException("Error en la búsqueda de categorías: " + e.getMessage());
        }
    }
    
    @GetMapping("/name/{categoryName}")
    @Operation(summary = "Obtener categoría por nombre", description = "Obtiene una categoría por su nombre")
    public ResponseEntity<Category> getCategoryByName(
            @Parameter(description = "Nombre de la categoría") @PathVariable String categoryName,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            return categoryService.getCategoryByName(categoryName, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categoría por nombre: " + e.getMessage());
        }
    }
    
    //  MÉTODOS DE ANÁLISIS 
    
    @GetMapping("/analytics/with-task-count")
    @Operation(summary = "Categorías con conteo de tareas", description = "Obtiene categorías con el número de tareas asociadas")
    public ResponseEntity<List<Object[]>> getCategoriesWithTaskCount(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            List<Object[]> categoriesWithCount = categoryService.getCategoriesWithTaskCount(userId);
            return ResponseEntity.ok(categoriesWithCount);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categorías con conteo: " + e.getMessage());
        }
    }
    
    @GetMapping("/analytics/most-used")
    @Operation(summary = "Categorías más usadas", description = "Obtiene las categorías más utilizadas")
    public ResponseEntity<List<Object[]>> getMostUsedCategories(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            List<Object[]> mostUsedCategories = categoryService.getMostUsedCategories(userId);
            return ResponseEntity.ok(mostUsedCategories);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categorías más usadas: " + e.getMessage());
        }
    }
    
    @GetMapping("/analytics/statistics")
    @Operation(summary = "Estadísticas de categorías", description = "Obtiene estadísticas generales de categorías")
    public ResponseEntity<Map<String, Object>> getCategoryStatistics(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            Map<String, Object> statistics = categoryService.getCategoryStatistics(userId);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas de categorías: " + e.getMessage());
        }
    }
    
    // MÉTODOS AUXILIARES
    
    @GetMapping("/exists/{categoryName}")
    @Operation(summary = "Verificar existencia", description = "Verifica si existe una categoría con el nombre especificado")
    public ResponseEntity<Map<String, Boolean>> categoryExists(
            @Parameter(description = "Nombre de la categoría") @PathVariable String categoryName,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            boolean exists = categoryService.categoryExists(categoryName, userId);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia: " + e.getMessage());
        }
    }
    
    @GetMapping("/exists/id/{categoryId}")
    @Operation(summary = "Verificar existencia por ID", description = "Verifica si existe una categoría con el ID especificado")
    public ResponseEntity<Map<String, Boolean>> categoryExistsById(
            @Parameter(description = "ID de la categoría") @PathVariable Integer categoryId,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            boolean exists = categoryService.categoryExistsById(categoryId, userId);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia por ID: " + e.getMessage());
        }
    }
    
    // MÉTODOS PARA CATEGORÍAS POR DEFECTO 
    
    @PostMapping("/default")
    @Operation(summary = "Crear categorías por defecto", description = "Crea las categorías por defecto para el usuario")
    public ResponseEntity<Map<String, String>> createDefaultCategories(HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            categoryService.createDefaultCategories(userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Categorías por defecto creadas exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear categorías por defecto: " + e.getMessage());
        }
    }
    
    private UUID getUserIdFromRequest(HttpServletRequest request) {
        return userService.getCurrentUserId();
    }
} 