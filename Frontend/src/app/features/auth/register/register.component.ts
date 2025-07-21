import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { AuthService, RegisterRequest } from '../../../core/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  registerForm: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.pattern(/^\+?[1-9]\d{1,14}$/)]],
      password: ['', [
        Validators.required, 
        Validators.minLength(8),
        this.passwordStrengthValidator()
      ]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordStrengthValidator(): (control: AbstractControl) => ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.value;
      if (!password) return null;

      const hasUpperCase = /[A-Z]/.test(password);
      const hasLowerCase = /[a-z]/.test(password);
      const hasNumbers = /\d/.test(password);
      const hasSpecialChar = /[@$!%*?&]/.test(password);

      const errors: ValidationErrors = {};
      
      if (!hasUpperCase) errors['missingUpperCase'] = true;
      if (!hasLowerCase) errors['missingLowerCase'] = true;
      if (!hasNumbers) errors['missingNumbers'] = true;
      if (!hasSpecialChar) errors['missingSpecialChar'] = true;

      return Object.keys(errors).length > 0 ? errors : null;
    };
  }

  private passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password');
    const confirmPassword = group.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      
      const registerRequest: RegisterRequest = {
        firstName: this.registerForm.get('firstName')?.value,
        lastName: this.registerForm.get('lastName')?.value,
        email: this.registerForm.get('email')?.value,
        phoneNumber: this.registerForm.get('phoneNumber')?.value || undefined,
        password: this.registerForm.get('password')?.value
      };

      this.authService.register(registerRequest).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.snackBar.open('Registro exitoso. Bienvenido!', 'Cerrar', {
            duration: 3000,
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
        },
        error: (error) => {
          this.isLoading = false;
          this.snackBar.open(error, 'Cerrar', {
            duration: 5000,
            horizontalPosition: 'center',
            verticalPosition: 'top',
            panelClass: ['error-snackbar']
          });
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  getErrorMessage(fieldName: string): string {
    const field = this.registerForm.get(fieldName);
    
    if (field?.hasError('required')) {
      return `${this.getFieldDisplayName(fieldName)} es requerido`;
    }
    
    if (fieldName === 'email' && field?.hasError('email')) {
      return 'Formato de email inválido';
    }
    
    if (fieldName === 'phoneNumber' && field?.hasError('pattern')) {
      return 'Formato de teléfono inválido';
    }
    
    if (fieldName === 'firstName' && field?.hasError('maxlength')) {
      return 'El nombre no puede exceder 100 caracteres';
    }
    
    if (fieldName === 'lastName' && field?.hasError('maxlength')) {
      return 'El apellido no puede exceder 100 caracteres';
    }
    
    if (fieldName === 'password') {
      if (field?.hasError('minlength')) {
        return 'La contraseña debe tener al menos 8 caracteres';
      }
      if (field?.hasError('missingUpperCase')) {
        return 'La contraseña debe contener al menos una letra mayúscula';
      }
      if (field?.hasError('missingLowerCase')) {
        return 'La contraseña debe contener al menos una letra minúscula';
      }
      if (field?.hasError('missingNumbers')) {
        return 'La contraseña debe contener al menos un número';
      }
      if (field?.hasError('missingSpecialChar')) {
        return 'La contraseña debe contener al menos un carácter especial (@$!%*?&)';
      }
    }
    
    if (fieldName === 'confirmPassword' && field?.hasError('required')) {
      return 'Confirma tu contraseña';
    }
    
    return '';
  }

  getFormError(): string {
    if (this.registerForm.hasError('passwordMismatch')) {
      return 'Las contraseñas no coinciden';
    }
    return '';
  }

  private getFieldDisplayName(fieldName: string): string {
    const names: { [key: string]: string } = {
      firstName: 'Nombre',
      lastName: 'Apellido',
      email: 'Email',
      phoneNumber: 'Teléfono',
      password: 'Contraseña',
      confirmPassword: 'Confirmar contraseña'
    };
    return names[fieldName] || fieldName;
  }

  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.hideConfirmPassword = !this.hideConfirmPassword;
  }

  getPasswordStrength(): { score: number; message: string; color: string } {
    const password = this.registerForm.get('password')?.value;
    if (!password) return { score: 0, message: '', color: '' };

    let score = 0;
    if (password.length >= 8) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/[a-z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[@$!%*?&]/.test(password)) score++;

    const messages = ['Muy débil', 'Débil', 'Media', 'Fuerte', 'Muy fuerte'];
    const colors = ['#ff4444', '#ff8800', '#ffbb33', '#00C851', '#007E33'];

    return {
      score: Math.min(score, 5),
      message: messages[score - 1] || '',
      color: colors[score - 1] || ''
    };
  }
}

