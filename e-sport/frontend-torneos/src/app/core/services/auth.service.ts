import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, AuthResponse, RefreshTokenRequest, RefreshTokenResponse, User } from '../models/auth.models';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
  ) {
    this.loadCurrentUser();
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          this.tokenService.setTokens(response.accessToken, response.refreshToken);
          this.currentUserSubject.next(response.user);
        })
      );
  }

  refreshToken(): Observable<RefreshTokenResponse> {
    const refreshToken = this.tokenService.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const request: RefreshTokenRequest = { refreshToken };
    return this.http.post<RefreshTokenResponse>(`${this.apiUrl}/auth/refresh`, request)
      .pipe(
        tap(response => {
          this.tokenService.setTokens(response.accessToken, refreshToken);
        })
      );
  }

  logout(): void {
    this.tokenService.clearTokens();
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return this.tokenService.hasValidToken();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  private loadCurrentUser(): void {
    if (this.isAuthenticated()) {
      const token = this.tokenService.getAccessToken();
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          // Crear un usuario temporal con los datos del token
          const tempUser = {
            id: payload.sub,
            email: payload.sub,
            fullName: payload.fullName || '',
            role: payload.role,
            createdAt: '',
            updatedAt: ''
          };
          this.currentUserSubject.next(tempUser);
          
          // Intentar obtener los datos completos del usuario desde el backend
          this.getUserProfile().subscribe({
            next: (fullUser) => {
              this.currentUserSubject.next(fullUser);
            },
            error: (error) => {
              console.warn('Could not load full user profile, using token data:', error);
              // Mantener el usuario temporal si no se puede cargar el perfil completo
            }
          });
        } catch (error) {
          console.error('Error parsing token:', error);
          this.logout();
        }
      }
    }
  }

  private getUserProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/auth/profile`);
  }
}