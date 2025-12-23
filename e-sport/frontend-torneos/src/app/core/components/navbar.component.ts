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
      <div class="container navbar-content">
        <!-- Brand -->
        <div class="nav-brand">
          <div class="brand-icon">
            <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
              <path d="M16 2L20.5 10.5L30 12L22 20L24 30L16 25L8 30L10 20L2 12L11.5 10.5L16 2Z" 
                    fill="url(#gradient)" stroke="currentColor" stroke-width="1"/>
              <defs>
                <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#6366f1"/>
                  <stop offset="100%" stop-color="#8b5cf6"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h3 class="brand-text">Torneos E-Sport</h3>
        </div>
        
        <!-- Mobile menu button -->
        <button class="mobile-menu-btn" (click)="toggleMobileMenu()" *ngIf="currentUser">
          <span class="hamburger" [class.active]="mobileMenuOpen"></span>
        </button>
        
        <!-- Navigation Links -->
        <div class="nav-links" [class.mobile-open]="mobileMenuOpen" *ngIf="currentUser">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-item" (click)="closeMobileMenu()">
            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
              <path d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z"/>
            </svg>
            Dashboard
          </a>
          <a routerLink="/tournaments" routerLinkActive="active" class="nav-item" (click)="closeMobileMenu()">
            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            Torneos
          </a>
          <a routerLink="/tickets" routerLinkActive="active" class="nav-item" (click)="closeMobileMenu()">
            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
              <path d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 11-4 0V7a2 2 0 00-2-2H5z"/>
            </svg>
            Mis Tickets
          </a>
          <a routerLink="/users" routerLinkActive="active" class="nav-item" (click)="closeMobileMenu()">
            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"/>
            </svg>
            Usuarios
          </a>
          <a routerLink="/validation" routerLinkActive="active" class="nav-item" (click)="closeMobileMenu()">
            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
            </svg>
            Validar
          </a>
          <a routerLink="/categories" routerLinkActive="active" class="nav-item" (click)="closeMobileMenu()">
            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
              <path d="M7 3a1 1 0 000 2h6a1 1 0 100-2H7zM4 7a1 1 0 011-1h10a1 1 0 110 2H5a1 1 0 01-1-1zM2 11a2 2 0 012-2h12a2 2 0 012 2v4a2 2 0 01-2 2H4a2 2 0 01-2-2v-4z"/>
            </svg>
            Categorías
          </a>
          <a routerLink="/game-types" routerLinkActive="active" class="nav-item" (click)="closeMobileMenu()">
            <svg width="20" height="20" fill="currentColor" viewBox="0 0 20 20">
              <path d="M11 17a1 1 0 001.447.894l4-2A1 1 0 0017 15V9.236a1 1 0 00-1.447-.894l-4 2a1 1 0 000 1.788l4 2.016V13a1 1 0 11-2 0v-1.382l-4-2.016V17zM15.211 6.276a1 1 0 000-1.788l-4.764-2.382a1 1 0 00-.894 0L4.789 4.488a1 1 0 000 1.788L9.553 8.658a1 1 0 00.894 0l4.764-2.382zM5.553 9.658L6 9.882v1.618a1 1 0 11-2 0V9.236a1 1 0 011.447-.894l4 2a1 1 0 000 1.788l-4 2.016V13a1 1 0 11-2 0v-1.382l-4-2.016z"/>
            </svg>
            Juegos
          </a>
        </div>

        <!-- User Menu -->
        <div class="nav-user" *ngIf="currentUser">
          <div class="user-avatar" (click)="toggleUserMenu()">
            <div class="avatar-circle">
              {{ getUserInitials() }}
            </div>
            <div class="user-details">
              <span class="user-name">{{ currentUser.fullName || currentUser.email }}</span>
              <span class="user-role badge badge-info">{{ currentUser.role }}</span>
            </div>
            <svg class="dropdown-arrow" [class.rotated]="userMenuOpen" width="16" height="16" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd"/>
            </svg>
          </div>
          
          <!-- User Dropdown -->
          <div class="user-dropdown" [class.open]="userMenuOpen">
            <div class="dropdown-header">
              <div class="avatar-circle-large">
                {{ getUserInitials() }}
              </div>
              <div>
                <div class="dropdown-name">{{ currentUser.fullName || currentUser.email }}</div>
                <div class="dropdown-email">{{ currentUser.email }}</div>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <button class="dropdown-item" (click)="logout()">
              <svg width="16" height="16" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M3 3a1 1 0 00-1 1v12a1 1 0 102 0V4a1 1 0 00-1-1zm10.293 9.293a1 1 0 001.414 1.414l3-3a1 1 0 000-1.414l-3-3a1 1 0 10-1.414 1.414L14.586 9H7a1 1 0 100 2h7.586l-1.293 1.293z" clip-rule="evenodd"/>
              </svg>
              Cerrar Sesión
            </button>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background: var(--color-bg-secondary);
      backdrop-filter: blur(10px);
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      position: sticky;
      top: 0;
      z-index: 1000;
      box-shadow: var(--shadow-lg);
    }
    
    .navbar-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: var(--spacing-lg) 0;
      position: relative;
    }
    
    .nav-brand {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
      cursor: pointer;
    }
    
    .brand-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      border-radius: var(--border-radius-lg);
      background: var(--gradient-primary);
      color: white;
      box-shadow: var(--shadow-md);
    }
    
    .brand-text {
      font-family: var(--font-gaming);
      font-size: var(--font-size-xl);
      font-weight: var(--font-weight-bold);
      background: var(--gradient-primary);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin: 0;
    }
    
    .mobile-menu-btn {
      display: none;
      flex-direction: column;
      justify-content: center;
      width: 40px;
      height: 40px;
      background: transparent;
      border: none;
      cursor: pointer;
      padding: 0;
    }
    
    .hamburger {
      width: 24px;
      height: 2px;
      background: var(--color-text-primary);
      transition: all 0.3s ease;
      position: relative;
    }
    
    .hamburger::before,
    .hamburger::after {
      content: '';
      position: absolute;
      width: 24px;
      height: 2px;
      background: var(--color-text-primary);
      transition: all 0.3s ease;
    }
    
    .hamburger::before {
      top: -8px;
    }
    
    .hamburger::after {
      bottom: -8px;
    }
    
    .hamburger.active {
      background: transparent;
    }
    
    .hamburger.active::before {
      top: 0;
      transform: rotate(45deg);
    }
    
    .hamburger.active::after {
      bottom: 0;
      transform: rotate(-45deg);
    }
    
    .nav-links {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
    }
    
    .nav-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      padding: var(--spacing-sm) var(--spacing-lg);
      border-radius: var(--border-radius-md);
      color: var(--color-text-secondary);
      font-size: var(--font-size-sm);
      font-weight: var(--font-weight-medium);
      transition: all var(--transition-fast);
      cursor: pointer;
      position: relative;
      overflow: hidden;
    }
    
    .nav-item::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: var(--gradient-primary);
      transition: left 0.3s ease;
      z-index: -1;
    }
    
    .nav-item:hover {
      color: var(--color-text-primary);
      transform: translateY(-1px);
    }
    
    .nav-item:hover::before {
      left: 0;
    }
    
    .nav-item.active {
      background: var(--gradient-primary);
      color: white;
      box-shadow: var(--shadow-glow);
    }
    
    .nav-item svg {
      width: 18px;
      height: 18px;
    }
    
    .nav-user {
      position: relative;
    }
    
    .user-avatar {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
      padding: var(--spacing-sm);
      border-radius: var(--border-radius-lg);
      cursor: pointer;
      transition: all var(--transition-fast);
    }
    
    .user-avatar:hover {
      background: rgba(255, 255, 255, 0.05);
    }
    
    .avatar-circle {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: var(--gradient-primary);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-weight: var(--font-weight-semibold);
      font-size: var(--font-size-sm);
      box-shadow: var(--shadow-md);
    }
    
    .avatar-circle-large {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      background: var(--gradient-primary);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-weight: var(--font-weight-semibold);
      font-size: var(--font-size-lg);
      box-shadow: var(--shadow-md);
    }
    
    .user-details {
      display: flex;
      flex-direction: column;
      gap: var(--spacing-xs);
    }
    
    .user-name {
      font-size: var(--font-size-sm);
      font-weight: var(--font-weight-medium);
      color: var(--color-text-primary);
    }
    
    .dropdown-arrow {
      transition: transform var(--transition-fast);
      color: var(--color-text-secondary);
    }
    
    .dropdown-arrow.rotated {
      transform: rotate(180deg);
    }
    
    .user-dropdown {
      position: absolute;
      top: 100%;
      right: 0;
      margin-top: var(--spacing-sm);
      background: var(--color-bg-card);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: var(--border-radius-lg);
      box-shadow: var(--shadow-xl);
      min-width: 280px;
      opacity: 0;
      visibility: hidden;
      transform: translateY(-10px);
      transition: all var(--transition-fast);
      z-index: 1000;
    }
    
    .user-dropdown.open {
      opacity: 1;
      visibility: visible;
      transform: translateY(0);
    }
    
    .dropdown-header {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
      padding: var(--spacing-lg);
    }
    
    .dropdown-name {
      font-size: var(--font-size-sm);
      font-weight: var(--font-weight-semibold);
      color: var(--color-text-primary);
    }
    
    .dropdown-email {
      font-size: var(--font-size-xs);
      color: var(--color-text-muted);
    }
    
    .dropdown-divider {
      height: 1px;
      background: rgba(255, 255, 255, 0.1);
      margin: 0 var(--spacing-lg);
    }
    
    .dropdown-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
      width: 100%;
      padding: var(--spacing-md) var(--spacing-lg);
      background: transparent;
      border: none;
      color: var(--color-text-secondary);
      font-size: var(--font-size-sm);
      cursor: pointer;
      transition: all var(--transition-fast);
    }
    
    .dropdown-item:hover {
      background: rgba(239, 68, 68, 0.1);
      color: var(--color-danger);
    }
    
    .dropdown-item:last-child {
      border-radius: 0 0 var(--border-radius-lg) var(--border-radius-lg);
    }
    
    @media (max-width: 1024px) {
      .mobile-menu-btn {
        display: flex;
      }
      
      .nav-links {
        position: absolute;
        top: 100%;
        left: 0;
        right: 0;
        background: var(--color-bg-secondary);
        border-top: 1px solid rgba(255, 255, 255, 0.1);
        flex-direction: column;
        padding: var(--spacing-lg);
        gap: var(--spacing-sm);
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: all var(--transition-fast);
      }
      
      .nav-links.mobile-open {
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
      }
      
      .nav-item {
        width: 100%;
        justify-content: flex-start;
      }
      
      .user-details {
        display: none;
      }
    }
    
    @media (max-width: 768px) {
      .brand-text {
        display: none;
      }
      
      .user-dropdown {
        right: -100px;
        min-width: 250px;
      }
    }
  `]
})
export class NavbarComponent {
  currentUser: User | null = null;
  mobileMenuOpen = false;
  userMenuOpen = false;

  constructor(private authService: AuthService) {
    // Suscribirse inmediatamente al observable del usuario
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    // También obtener el usuario actual si ya existe
    this.currentUser = this.authService.getCurrentUser();
    
    // Cerrar menús al hacer click fuera
    document.addEventListener('click', (event) => {
      const target = event.target as HTMLElement;
      if (!target.closest('.nav-user') && !target.closest('.nav-links')) {
        this.userMenuOpen = false;
        this.mobileMenuOpen = false;
      }
    });
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
    this.userMenuOpen = false;
  }

  toggleUserMenu(): void {
    this.userMenuOpen = !this.userMenuOpen;
    this.mobileMenuOpen = false;
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen = false;
  }

  getUserInitials(): string {
    if (!this.currentUser) return 'U';
    
    const name = this.currentUser.fullName || this.currentUser.email;
    const words = name.split(' ');
    
    if (words.length >= 2) {
      return (words[0][0] + words[1][0]).toUpperCase();
    }
    
    return name.substring(0, 2).toUpperCase();
  }

  logout(): void {
    this.authService.logout();
    this.userMenuOpen = false;
    window.location.href = '/login';
  }
}