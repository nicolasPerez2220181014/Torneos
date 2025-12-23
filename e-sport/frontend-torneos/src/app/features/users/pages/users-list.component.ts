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
    .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .filters { margin-bottom: 20px; }
    .filter-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 10px; }
    .users-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 20px; margin-bottom: 20px; }
    .user-card { border: 1px solid #ddd; border-radius: 8px; padding: 20px; background: white; }
    .user-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
    .user-header h3 { margin: 0; }
    .role { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
    .role-user { background: #e8f5e8; color: #2e7d32; }
    .role-organizer { background: #e3f2fd; color: #1976d2; }
    .role-subadmin { background: #f3e5f5; color: #7b1fa2; }
    .user-info { margin-bottom: 15px; }
    .info-item { margin-bottom: 8px; font-size: 14px; }
    .user-actions { display: flex; gap: 10px; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 20px; }
    .page-info { font-size: 14px; color: #666; }
    .loading, .error { text-align: center; padding: 40px; }
    .error { color: #d32f2f; }
    .btn { padding: 8px 16px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-outline { background: white; }
    .btn-sm { padding: 4px 8px; font-size: 12px; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .form-control { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
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