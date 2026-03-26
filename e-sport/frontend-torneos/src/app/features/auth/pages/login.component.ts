import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../core/models/auth.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-page">
      <div class="login-left">
        <div class="brand">
          <h1>TORNEOS</h1>
          <span>E-SPORT</span>
        </div>
        <p class="tagline">Plataforma de gestión de torneos competitivos</p>
      </div>

      <div class="login-right">
        <div class="login-card">
          <h2>Iniciar Sesión</h2>
          <p class="subtitle">Ingresa tus credenciales para continuar</p>

          <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
            <div class="form-group">
              <label for="email">Email</label>
              <input
                type="email"
                id="email"
                name="email"
                [(ngModel)]="credentials.email"
                required
                email
                #email="ngModel"
                class="form-control"
                placeholder="tu@email.com">
              <div *ngIf="email.invalid && email.touched" class="field-error">
                Email es requerido y debe ser válido
              </div>
            </div>

            <div class="form-group">
              <label for="password">Contraseña</label>
              <input
                type="password"
                id="password"
                name="password"
                [(ngModel)]="credentials.password"
                required
                minlength="6"
                #password="ngModel"
                class="form-control"
                placeholder="Min. 6 caracteres">
              <div *ngIf="password.invalid && password.touched" class="field-error">
                Contraseña es requerida (mínimo 6 caracteres)
              </div>
            </div>

            <div *ngIf="error" class="error-banner">
              {{ error }}
            </div>

            <button
              type="submit"
              [disabled]="loginForm.invalid || loading"
              class="btn btn-primary btn-submit">
              <span *ngIf="loading" class="spinner"></span>
              {{ loading ? 'Ingresando...' : 'Ingresar' }}
            </button>
          </form>

          <div class="demo-box">
            <span class="demo-label">Demo</span>
            <div>admin&#64;torneos.com / admin123</div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-page {
      display: flex;
      min-height: 100vh;
    }

    .login-left {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      background: linear-gradient(135deg, var(--bg-primary) 0%, #1a1040 100%);
      padding: 48px;
      position: relative;
      overflow: hidden;
    }
    .login-left::before {
      content: '';
      position: absolute;
      width: 400px;
      height: 400px;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(108,92,231,0.15) 0%, transparent 70%);
      top: 20%;
      left: 30%;
    }

    .brand {
      text-align: center;
      position: relative;
    }
    .brand h1 {
      font-family: var(--font-gaming);
      font-size: 3rem;
      font-weight: 900;
      color: var(--text-primary);
      letter-spacing: 6px;
      margin-bottom: 4px;
    }
    .brand span {
      font-family: var(--font-gaming);
      font-size: 1rem;
      color: var(--accent);
      letter-spacing: 12px;
      font-weight: 600;
    }
    .tagline {
      margin-top: 24px;
      color: var(--text-muted);
      font-size: 0.9rem;
      text-align: center;
      position: relative;
    }

    .login-right {
      width: 480px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--bg-secondary);
      padding: 48px;
    }

    .login-card {
      width: 100%;
      max-width: 360px;
    }
    .login-card h2 {
      font-size: 1.5rem;
      margin-bottom: 4px;
    }
    .subtitle {
      color: var(--text-muted);
      font-size: 0.85rem;
      margin-bottom: 32px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .field-error {
      color: var(--danger);
      font-size: 0.75rem;
      margin-top: 6px;
    }

    .error-banner {
      background: var(--danger-soft);
      color: var(--danger);
      padding: 10px 14px;
      border-radius: var(--radius-sm);
      font-size: 0.8rem;
      margin-bottom: 16px;
      border: 1px solid rgba(231, 76, 60, 0.2);
    }

    .btn-submit {
      width: 100%;
      padding: 12px;
      font-size: 0.9rem;
      margin-top: 8px;
    }

    .demo-box {
      margin-top: 24px;
      padding: 12px 16px;
      background: var(--bg-input);
      border: 1px solid var(--border);
      border-radius: var(--radius-sm);
      font-size: 0.8rem;
      color: var(--text-muted);
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .demo-label {
      background: var(--accent-soft);
      color: var(--accent);
      padding: 2px 8px;
      border-radius: var(--radius-full);
      font-size: 0.65rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    @media (max-width: 768px) {
      .login-page { flex-direction: column; }
      .login-left { padding: 32px; min-height: 200px; }
      .brand h1 { font-size: 2rem; }
      .login-right { width: 100%; padding: 32px; }
    }
  `]
})
export class LoginComponent {
  credentials: LoginRequest = {
    email: '',
    password: ''
  };
  
  loading = false;
  error: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (this.loading) return;

    this.loading = true;
    this.error = null;

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        this.loading = false;
        this.router.navigate(['/tournaments']);
      },
      error: (error) => {
        this.loading = false;
        this.error = error.message || 'Error al iniciar sesión';
      }
    });
  }
}