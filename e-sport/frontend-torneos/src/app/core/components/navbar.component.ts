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
    <nav class="navbar" *ngIf="currentUser">
      <div class="nav-inner">
        <a routerLink="/tournaments" class="nav-brand">
          <span class="brand-mark">T</span>
          <span class="brand-text">Torneos</span>
        </a>

        <button class="mobile-toggle" (click)="toggleMobileMenu()">
          <span class="bar" [class.open]="mobileMenuOpen"></span>
        </button>

        <div class="nav-links" [class.show]="mobileMenuOpen">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-link" (click)="closeMobileMenu()">Dashboard</a>
          <a routerLink="/tournaments" routerLinkActive="active" class="nav-link" (click)="closeMobileMenu()">Torneos</a>
          <a routerLink="/tickets" routerLinkActive="active" class="nav-link" (click)="closeMobileMenu()">Tickets</a>
          <a routerLink="/users" routerLinkActive="active" class="nav-link" (click)="closeMobileMenu()">Usuarios</a>
          <a routerLink="/validation" routerLinkActive="active" class="nav-link" (click)="closeMobileMenu()">Validar</a>
          <a routerLink="/categories" routerLinkActive="active" class="nav-link" (click)="closeMobileMenu()">Categorías</a>
          <a routerLink="/game-types" routerLinkActive="active" class="nav-link" (click)="closeMobileMenu()">Juegos</a>
        </div>

        <div class="nav-user">
          <button class="user-btn" (click)="toggleUserMenu()">
            <span class="avatar">{{ getUserInitials() }}</span>
            <span class="user-name">{{ currentUser.fullName || currentUser.email }}</span>
            <svg class="chevron" [class.open]="userMenuOpen" width="14" height="14" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd"/>
            </svg>
          </button>

          <div class="dropdown" [class.open]="userMenuOpen">
            <div class="dropdown-info">
              <span class="avatar avatar-lg">{{ getUserInitials() }}</span>
              <div>
                <div class="dropdown-name">{{ currentUser.fullName || currentUser.email }}</div>
                <div class="dropdown-email">{{ currentUser.email }}</div>
                <span class="role-badge">{{ currentUser.role }}</span>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <button class="dropdown-action" (click)="logout()">Cerrar sesión</button>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background: var(--bg-secondary);
      border-bottom: 1px solid var(--border);
      position: sticky;
      top: 0;
      z-index: 100;
    }

    .nav-inner {
      max-width: 1280px;
      margin: 0 auto;
      padding: 0 var(--sp-xl);
      height: 56px;
      display: flex;
      align-items: center;
      gap: var(--sp-xl);
    }

    .nav-brand {
      display: flex;
      align-items: center;
      gap: var(--sp-sm);
      text-decoration: none;
      flex-shrink: 0;
    }
    .brand-mark {
      width: 32px;
      height: 32px;
      background: var(--accent);
      color: #fff;
      border-radius: var(--radius-md);
      display: flex;
      align-items: center;
      justify-content: center;
      font-family: var(--font-gaming);
      font-weight: 800;
      font-size: 0.9rem;
    }
    .brand-text {
      font-family: var(--font-gaming);
      font-size: 0.95rem;
      font-weight: 700;
      color: var(--text-primary);
      letter-spacing: 1px;
    }

    /* Mobile toggle */
    .mobile-toggle {
      display: none;
      background: none;
      border: none;
      cursor: pointer;
      padding: 8px;
    }
    .bar {
      display: block;
      width: 20px;
      height: 2px;
      background: var(--text-primary);
      position: relative;
      transition: background 0.2s;
    }
    .bar::before, .bar::after {
      content: '';
      position: absolute;
      width: 20px;
      height: 2px;
      background: var(--text-primary);
      transition: all 0.2s;
    }
    .bar::before { top: -6px; }
    .bar::after { top: 6px; }
    .bar.open { background: transparent; }
    .bar.open::before { top: 0; transform: rotate(45deg); }
    .bar.open::after { top: 0; transform: rotate(-45deg); }

    /* Nav links */
    .nav-links {
      display: flex;
      align-items: center;
      gap: 2px;
      flex: 1;
    }

    .nav-link {
      padding: 6px 14px;
      font-size: 0.8rem;
      font-weight: 500;
      color: var(--text-muted);
      text-decoration: none;
      border-radius: var(--radius-sm);
      transition: all 0.15s var(--ease);
      white-space: nowrap;
    }
    .nav-link:hover {
      color: var(--text-primary);
      background: rgba(255,255,255,0.04);
    }
    .nav-link.active {
      color: var(--accent);
      background: var(--accent-soft);
    }

    /* User */
    .nav-user {
      position: relative;
      flex-shrink: 0;
    }

    .user-btn {
      display: flex;
      align-items: center;
      gap: var(--sp-sm);
      background: none;
      border: 1px solid var(--border);
      border-radius: var(--radius-full);
      padding: 4px 12px 4px 4px;
      cursor: pointer;
      transition: all 0.15s var(--ease);
      color: var(--text-secondary);
      font-family: var(--font-body);
    }
    .user-btn:hover {
      border-color: var(--border-hover);
      background: rgba(255,255,255,0.03);
    }

    .avatar {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: var(--accent);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 0.7rem;
      font-weight: 600;
    }
    .avatar-lg {
      width: 40px;
      height: 40px;
      font-size: 0.85rem;
    }

    .user-name {
      font-size: 0.8rem;
      font-weight: 500;
      color: var(--text-primary);
    }

    .chevron {
      color: var(--text-muted);
      transition: transform 0.2s;
    }
    .chevron.open { transform: rotate(180deg); }

    /* Dropdown */
    .dropdown {
      position: absolute;
      top: calc(100% + 8px);
      right: 0;
      width: 280px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      box-shadow: var(--shadow-xl);
      opacity: 0;
      visibility: hidden;
      transform: translateY(-4px);
      transition: all 0.15s var(--ease);
      z-index: 200;
    }
    .dropdown.open {
      opacity: 1;
      visibility: visible;
      transform: translateY(0);
    }

    .dropdown-info {
      display: flex;
      align-items: center;
      gap: var(--sp-md);
      padding: var(--sp-lg);
    }
    .dropdown-name {
      font-size: 0.85rem;
      font-weight: 600;
      color: var(--text-primary);
    }
    .dropdown-email {
      font-size: 0.75rem;
      color: var(--text-muted);
      margin-bottom: 4px;
    }
    .role-badge {
      display: inline-block;
      padding: 1px 8px;
      font-size: 0.6rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      background: var(--accent-soft);
      color: var(--accent);
      border-radius: var(--radius-full);
    }

    .dropdown-divider {
      height: 1px;
      background: var(--border);
      margin: 0 var(--sp-lg);
    }

    .dropdown-action {
      display: block;
      width: 100%;
      padding: var(--sp-md) var(--sp-lg);
      background: none;
      border: none;
      text-align: left;
      font-size: 0.8rem;
      font-family: var(--font-body);
      color: var(--text-secondary);
      cursor: pointer;
      transition: all 0.15s;
      border-radius: 0 0 var(--radius-lg) var(--radius-lg);
    }
    .dropdown-action:hover {
      background: var(--danger-soft);
      color: var(--danger);
    }

    /* Responsive */
    @media (max-width: 1024px) {
      .mobile-toggle { display: block; }
      .user-name { display: none; }

      .nav-links {
        position: absolute;
        top: 56px;
        left: 0;
        right: 0;
        background: var(--bg-secondary);
        border-bottom: 1px solid var(--border);
        flex-direction: column;
        padding: var(--sp-md) var(--sp-xl);
        gap: 2px;
        display: none;
      }
      .nav-links.show { display: flex; }
      .nav-link { width: 100%; padding: 10px 14px; }
    }

    @media (max-width: 640px) {
      .brand-text { display: none; }
      .dropdown { right: -40px; width: 260px; }
    }
  `]
})
export class NavbarComponent {
  currentUser: User | null = null;
  mobileMenuOpen = false;
  userMenuOpen = false;

  constructor(private authService: AuthService) {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    this.currentUser = this.authService.getCurrentUser();

    document.addEventListener('click', (event) => {
      const target = event.target as HTMLElement;
      if (!target.closest('.nav-user') && !target.closest('.nav-links') && !target.closest('.mobile-toggle')) {
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
