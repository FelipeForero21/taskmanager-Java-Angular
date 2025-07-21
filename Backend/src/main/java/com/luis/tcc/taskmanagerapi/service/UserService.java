package com.luis.tcc.taskmanagerapi.service;

import com.luis.tcc.taskmanagerapi.dto.AuthRequest;
import com.luis.tcc.taskmanagerapi.dto.AuthResponse;
import com.luis.tcc.taskmanagerapi.entity.User;
import com.luis.tcc.taskmanagerapi.entity.UserPreferences;
import com.luis.tcc.taskmanagerapi.entity.UserSession;
import com.luis.tcc.taskmanagerapi.repository.UserPreferencesRepository;
import com.luis.tcc.taskmanagerapi.repository.UserRepository;
import com.luis.tcc.taskmanagerapi.repository.UserSessionRepository;
import com.luis.tcc.taskmanagerapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
    
    @Transactional
    public AuthResponse register(AuthRequest request) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Crear nuevo usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        // Crear preferencias por defecto
        UserPreferences preferences = new UserPreferences();
        preferences.setUserId(savedUser.getUserId());
        userPreferencesRepository.save(preferences);
        
        // Generar token JWT
        String token = jwtService.generateToken(savedUser);
        
        // Crear sesión
        createUserSession(savedUser, token);
        
        return buildAuthResponse(token, savedUser);
    }
    
    @Transactional
    public AuthResponse login(AuthRequest request) {
        // Autenticar usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        // Obtener usuario
        User user = userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Actualizar último login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Generar token JWT
        String token = jwtService.generateToken(user);
        
        // Crear sesión
        createUserSession(user, token);
        
        return buildAuthResponse(token, user);
    }
    
    @Transactional
    public void logout(String token) {
        // Invalidar sesión
        String tokenHash = passwordEncoder.encode(token);
        userSessionRepository.findByTokenHashAndIsActiveTrue(tokenHash)
                .ifPresent(session -> {
                    session.setIsActive(false);
                    userSessionRepository.save(session);
                });
    }
    
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email);
    }
    
    private void createUserSession(User user, String token) {
        String tokenHash = passwordEncoder.encode(token);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 24 horas
        
        UserSession session = new UserSession();
        session.setUserId(user.getUserId());
        session.setTokenHash(tokenHash);
        session.setIsActive(true);
        session.setExpiresAt(expiresAt);
        
        userSessionRepository.save(session);
    }
    
    private AuthResponse buildAuthResponse(String token, User user) {
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(86400000L) 
                .user(userInfo)
                .message("Autenticación exitosa")
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = loadUserByUsername(username);
            return jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }
    
    /** Obtiene el usuario actual desde el contexto de seguridad*/
    public User getCurrentUser() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmailAndIsActiveTrue(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuario actual: " + e.getMessage());
        }
    }
    
    /** Obtiene el userId del usuario actual*/
    public UUID getCurrentUserId() {
        return getCurrentUser().getUserId();
    }
} 