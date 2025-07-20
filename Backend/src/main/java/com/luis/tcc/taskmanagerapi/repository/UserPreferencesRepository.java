package com.luis.tcc.taskmanagerapi.repository;

import com.luis.tcc.taskmanagerapi.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {
    
    // Buscar preferencias por usuario
    Optional<UserPreferences> findByUserId(UUID userId);
    
    // Verificar si existen preferencias para un usuario
    boolean existsByUserId(UUID userId);
} 