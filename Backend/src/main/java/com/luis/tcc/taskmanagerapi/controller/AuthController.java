package com.luis.tcc.taskmanagerapi.controller;

import com.luis.tcc.taskmanagerapi.dto.AuthRequest;
import com.luis.tcc.taskmanagerapi.dto.AuthResponse;
import com.luis.tcc.taskmanagerapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "APIs para autenticación de usuarios")
@CrossOrigin(origins = {"https://taskmanager.tcc.com.co", "https://www.taskmanager.tcc.com.co"})
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario")

    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error en el registro: " + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")


    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error en el login: " + e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida el token JWT actual")


    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token != null) {
                userService.logout(token);
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sesión cerrada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Error en el logout: " + e.getMessage());
        }
    }
    
    @GetMapping("/validate")
    @Operation(summary = "Validar token", description = "Verifica si el token JWT es válido")

    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            boolean isValid = token != null && userService.validateToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("message", isValid ? "Token válido" : "Token inválido");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Error al validar token: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 