package com.luis.tcc.taskmanagerapi.repository;

import com.luis.tcc.taskmanagerapi.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    
    // Buscar sesión activa por token hash
    Optional<UserSession> findByTokenHashAndIsActiveTrue(String tokenHash);
    
    // Buscar sesiones activas de un usuario
    List<UserSession> findByUserIdAndIsActiveTrue(UUID userId);
    
    // Buscar sesiones expiradas
    @Query("SELECT us FROM UserSession us WHERE us.expiresAt < :now")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);
    
    // Invalidar sesión específica
    @Modifying
    @Query("UPDATE UserSession us SET us.isActive = false WHERE us.sessionId = :sessionId")
    void invalidateSession(@Param("sessionId") UUID sessionId);
    
    // Actualizar último uso de sesión
    @Modifying
    @Query("UPDATE UserSession us SET us.lastUsedAt = :lastUsedAt WHERE us.sessionId = :sessionId")
    void updateLastUsed(@Param("sessionId") UUID sessionId, @Param("lastUsedAt") LocalDateTime lastUsedAt);
    
    // Limpiar sesiones expiradas
    @Modifying
    @Query("DELETE FROM UserSession us WHERE us.expiresAt < :now")
    void deleteExpiredSessions(@Param("now") LocalDateTime now);
} 