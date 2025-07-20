package com.luis.tcc.taskmanagerapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "La contraseña debe contener al menos una letra mayúscula, una minúscula, un número y un carácter especial"
    )
    private String password;
    
    // Campos adicionales para registro
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String firstName;
    
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String lastName;
    
    @Pattern(
        regexp = "^\\+?[1-9]\\d{1,14}$",
        message = "El número de teléfono no es válido"
    )
    private String phoneNumber;
} 