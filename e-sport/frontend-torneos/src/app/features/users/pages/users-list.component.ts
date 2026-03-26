import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UsersService } from '../services/users.service';
import { User, UserFilters, UserRole } from '../../../core/models/user.models';
import { PaginatedResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Gestión de Usuarios</h2>
        <button class="btn btn-primary" (click)="navigateToCreate()">
          Crear Usuario
        </button>
      </div>

      <!-- Filtros -->
      <div class="filters">
        <div class="filter-row">
          <input 
            type="text" 
            placeholder="Buscar por email..." 
            [(ngModel)]="filters.email"
            (input)="onFilterChange()"
            class="form-control">
          
          <input 
            type="text" 
            placeholder="Buscar por nombre..." 
            [(ngModel)]="filters.firstName"
            (input)="onFilterChange()"
            class="form-control">

          <select [(ngModel)]="filters.role" (change)="onFilterChange()" class="form-control">
            <option value="">Todos los roles</option>
            <option *ngFor="let role of roleOptions" [value]="role.value">
              {{role.label}}
            </option>
          </select>
        </div>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="loading">
        Cargando usuarios...
      </div>

      <!-- Error -->
      <div *ngIf="error" class="error">
        {{error}}
      </div>

      <!-- Lista de usuarios -->
      <div *ngIf="!loading && !error" class="users-grid">
        <div *ngFor="let user of users" class="user-card">
          <div class="user-header">
            <h3>{{user.firstName}} {{user.lastName}}</h3>
            <span class="role" [class]="'role-' + user.role.toLowerCase()">
              {{getRoleLabel(user.role)}}
            </span>
          </div>
          
          <div class="user-info">
            <div class="info-item">
              <strong>Email:</strong> {{user.email}}
            </div>
            <div class="info-item">
              <strong>Creado:</strong> {{formatDate(user.createdAt)}}
            </div>
          </div>

          <div class="user-actions">
            <button class="btn btn-sm btn-outline" (click)="viewUser(user.id)">
              Ver
            </button>
            <button class="btn btn-sm btn-outline" (click)="editUser(user.id)">
              Editar
            </button>
          </div>
        </div>
      </div>

      <!-- Paginación -->
      <div *ngIf="paginationData && paginationData.totalElements > 0" class="pagination">
        <button 
          class="btn btn-outline" 
          [disabled]="paginationData.first"
          (click)="goToPage(paginationData.number - 1)">
          Anterior
        </button>
        
        <span class="page-info">
          Página {{paginationData.number + 1}} de {{paginationData.totalPages}}
          ({{paginationData.totalElements}} total)
        </span>
        
        <button 
          class="btn btn-outline" 
          [disabled]="paginationData.last"
          (click)="goToPage(paginationData.number + 1)">
          Siguiente
        </button>
      </div>
    </div>
  `,
  styles: [`
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--sp-xl); }
    .header h2 { font-size: 1.4rem; }
    .filters { margin-bottom: var(--sp-xl); }
    .filter-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: var(--sp-sm); }
    .users-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: var(--sp-lg); margin-bottom: var(--sp-xl); }
    .user-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      padding: var(--sp-xl);
      transition: all 0.2s var(--ease);
    }
    .user-card:hover { border-color: var(--border-hover); box-shadow: var(--shadow-md); }
    .user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--sp-lg); }
    .user-header h3 { margin: 0; font-size: 1.05rem; color: var(--text-primary); }
    .role { padding: 3px 10px; border-radius: var(--radius-full); font-size: 0.7rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
    .role-user { background: var(--success-soft); color: var(--success); }
    .role-organizer { background: var(--info-soft); color: var(--info); }
    .role-subadmin { background: var(--accent-soft); color: var(--accent); }
    .user-info { margin-bottom: var(--sp-lg); }
    .info-item { margin-bottom: var(--sp-sm); font-size: 0.8rem; color: var(--text-secondary); }
    .info-item strong { color: var(--text-muted); font-weight: 500; }
    .user-actions { display: flex; gap: var(--sp-sm); padding-top: var(--sp-lg); border-top: 1px solid var(--border); }
    @media (max-width: 768px) {
      .filter-row { grid-template-columns: 1fr; }
      .users-grid { grid-template-columns: 1fr; }
    }
  `]
})
export class UsersListComponent implements OnInit {
  users: User[] = [];
  loading = false;
  error: string | null = null;
  paginationData: any = null;

  filters: UserFilters = {};
  currentPage = 0;
  pageSize = 12;

  roleOptions = [
    { value: UserRole.USER, label: 'Usuario' },
    { value: UserRole.ORGANIZER, label: 'Organizador' },
    { value: UserRole.SUBADMIN, label: 'Subadministrador' }
  ];

  constructor(
    private usersService: UsersService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    this.error = null;

    this.usersService.getUsers(this.currentPage, this.pageSize, this.filters).subscribe({
      next: (response: PaginatedResponse<User>) => {
        this.users = response.content;
        this.paginationData = response;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar los usuarios';
        this.loading = false;
        console.error('Error loading users:', error);
      }
    });
  }

  onFilterChange() {
    this.currentPage = 0;
    this.loadUsers();
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.loadUsers();
  }

  navigateToCreate() {
    this.router.navigate(['/users/create']);
  }

  viewUser(id: number) {
    this.router.navigate(['/users', id]);
  }

  editUser(id: number) {
    this.router.navigate(['/users', id, 'edit']);
  }

  getRoleLabel(role: UserRole): string {
    const roleLabels = {
      [UserRole.USER]: 'Usuario',
      [UserRole.ORGANIZER]: 'Organizador', 
      [UserRole.SUBADMIN]: 'Subadministrador'
    };
    return roleLabels[role] || role;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-ES');
  }
}