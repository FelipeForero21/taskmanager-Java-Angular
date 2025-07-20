package com.luis.tcc.taskmanagerapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UserId")
    private UUID userId;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Column(name = "Email", unique = true, nullable = false, length = 255)
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "PasswordHash", nullable = false, length = 500)
    private String passwordHash;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    @Column(name = "FirstName", nullable = false, length = 100)
    private String firstName;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 1, max = 100, message = "El apellido debe tener entre 1 y 100 caracteres")
    @Column(name = "LastName", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "UpdatedAt", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "LastLoginAt")
    private LocalDateTime lastLoginAt;
    
    @Column(name = "ProfileImageUrl", length = 500)
    private String profileImageUrl;
    
    @Column(name = "PhoneNumber", length = 20)
    private String phoneNumber;
    
    @Column(name = "TimeZone", nullable = false, length = 50)
    private String timeZone = "America/Bogota";
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Métodos de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public String getPassword() {
        return passwordHash;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive;
    }
    
    // Método helper para obtener el nombre completo
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 