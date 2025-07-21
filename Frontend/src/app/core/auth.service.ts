import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

export interface User {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  isActive: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: User;
  message: string;
  timestamp: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = environment.authUrl;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  private _isAuthenticated = signal<boolean>(false);
  private _currentUser = signal<User | null>(null);
  private _isLoading = signal<boolean>(false);

  public isAuthenticated = computed(() => this._isAuthenticated());
  public currentUser = computed(() => this._currentUser());
  public isLoading = computed(() => this._isLoading());

  private loginStateSubject = new BehaviorSubject<boolean>(false);
  public loginState$ = this.loginStateSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.initializeAuth();
  }

  private initializeAuth(): void {
    const token = this.getToken();
    const user = this.getStoredUser();
    
    if (token && user) {
      this._isAuthenticated.set(true);
      this._currentUser.set(user);
      this.loginStateSubject.next(true);
    }
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    this._isLoading.set(true);
    
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, request).pipe(
      tap(response => {
        this.handleAuthSuccess(response);
        this._isLoading.set(false);
      }),
      catchError(error => {
        this._isLoading.set(false);
        return throwError(() => this.handleAuthError(error));
      })
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    this._isLoading.set(true);
    
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, request).pipe(
      tap(response => {
        this.handleAuthSuccess(response);
        this._isLoading.set(false);
      }),
      catchError(error => {
        this._isLoading.set(false);
        return throwError(() => this.handleAuthError(error));
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.API_URL}/logout`, {}).pipe(
      tap(() => {
        this.clearAuth();
        this.router.navigate(['/auth/login']);
      }),
      catchError(error => {
        this.clearAuth();
        this.router.navigate(['/auth/login']);
        return throwError(() => error);
      })
    );
  }

  validateToken(): Observable<any> {
    return this.http.get(`${this.API_URL}/validate`);
  }

  private handleAuthSuccess(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(response.user));
    
    this._isAuthenticated.set(true);
    this._currentUser.set(response.user);
    this.loginStateSubject.next(true);
    
    this.router.navigate(['/dashboard']);
  }

  private handleAuthError(error: any): string {
    if (error.error?.message) {
      return error.error.message;
    } else if (error.status === 401) {
      return 'Credenciales inválidas';
    } else if (error.status === 400) {
      return 'Datos de entrada inválidos';
    } else if (error.status === 0) {
      return 'Error de conexión con el servidor';
    } else {
      return 'Error inesperado. Intente nuevamente.';
    }
  }

  private clearAuth(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    
    this._isAuthenticated.set(false);
    this._currentUser.set(null);
    this.loginStateSubject.next(false);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private getStoredUser(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  isLoggedIn(): boolean {
    return this._isAuthenticated();
  }

  getCurrentUser(): User | null {
    return this._currentUser();
  }

  getUserFullName(): string {
    const user = this._currentUser();
    if (user) {
      return `${user.firstName} ${user.lastName}`.trim();
    }
    return '';
  }

  getUserEmail(): string {
    const user = this._currentUser();
    return user?.email || '';
  }
} 