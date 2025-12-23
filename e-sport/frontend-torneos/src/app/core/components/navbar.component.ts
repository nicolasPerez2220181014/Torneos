import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { User } from '../models/auth.models';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar">
      <div class="nav-brand">
        <h3>Torneos E-Sport</h3>
      </div>
      
      <div class="nav-links" *ngIf="currentUser">
        <a routerLink="/tournaments" routerLinkActive="active">Torneos</a>
        <a routerLink="/tickets" routerLinkActive="active">Mis Tickets</a>
        <a routerLink="/users" routerLinkActive="active">Usuarios</a>
        <a routerLink="/validation" routerLinkActive="active">Validar Tickets</a>
        <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
        <a routerLink="/categories" routerLinkActive="active">Categorías</a>
        <a routerLink="/game-types" routerLinkActive="active">Tipos de Juego</a>
      </div>

      <div class="nav-user" *ngIf="currentUser">
        <span class="user-info">{{ currentUser.fullName || currentUser.email }}</span>
        <span class="user-role">{{ currentUser.role }}</span>
        <button class="btn-logout" (click)="logout()">Salir</button>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background: #343a40;
      color: white;
      padding: 1rem 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .nav-brand h3 {
      margin: 0;
      color: #007bff;
    }
    .nav-links {
      display: flex;
      gap: 2rem;
    }
    .nav-links a {
      color: #adb5bd;
      text-decoration: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      transition: all 0.2s;
    }
    .nav-links a:hover,
    .nav-links a.active {
      color: white;
      background: #495057;
    }
    .nav-user {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    .user-info {
      font-weight: bold;
    }
    .user-role {
      background: #007bff;
      padding: 0.25rem 0.5rem;
      border-radius: 12px;
      font-size: 0.75rem;
    }
    .btn-logout {
      background: #dc3545;
      color: white;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      cursor: pointer;
    }
    .btn-logout:hover {
      background: #c82333;
    }
  `]
})
export class NavbarComponent {
  currentUser: User | null = null;

  constructor(private authService: AuthService) {
    // Suscribirse inmediatamente al observable del usuario
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    // También obtener el usuario actual si ya existe
    this.currentUser = this.authService.getCurrentUser();
  }

  logout(): void {
    this.authService.logout();
    window.location.href = '/login';
  }
}