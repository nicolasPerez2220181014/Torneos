import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { UsersService } from '../services/users.service';
import { UserRole } from '../../../core/models/user.models';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>{{ isEdit ? 'Editar Usuario' : 'Crear Usuario' }}</h2>
        <button class="btn btn-outline" (click)="goBack()">
          Volver
        </button>
      </div>

      <div class="form-container">
        <form [formGroup]="userForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="email">Email *</label>
            <input 
              type="email" 
              id="email"
              formControlName="email"
              class="form-control"
              [class.error]="userForm.get('email')?.invalid && userForm.get('email')?.touched">
            <div class="error-message" *ngIf="userForm.get('email')?.invalid && userForm.get('email')?.touched">
              <span *ngIf="userForm.get('email')?.errors?.['required']">El email es requerido</span>
              <span *ngIf="userForm.get('email')?.errors?.['email']">Ingrese un email válido</span>
            </div>
          </div>

          <div class="form-group">
            <label for="fullName">Nombre Completo *</label>
            <input 
              type="text" 
              id="fullName"
              formControlName="fullName"
              class="form-control"
              placeholder="Ej: Juan Pérez"
              [class.error]="userForm.get('fullName')?.invalid && userForm.get('fullName')?.touched">
            <div class="error-message" *ngIf="userForm.get('fullName')?.invalid && userForm.get('fullName')?.touched">
              <span *ngIf="userForm.get('fullName')?.errors?.['required']">El nombre completo es requerido</span>
              <span *ngIf="userForm.get('fullName')?.errors?.['minlength']">El nombre debe tener al menos 2 caracteres</span>
            </div>
          </div>

          <div class="form-group">
            <label for="role">Rol *</label>
            <select 
              id="role"
              formControlName="role"
              class="form-control"
              [class.error]="userForm.get('role')?.invalid && userForm.get('role')?.touched">
              <option value="">Seleccione un rol</option>
              <option *ngFor="let role of roleOptions" [value]="role.value">
                {{role.label}}
              </option>
            </select>
            <div class="error-message" *ngIf="userForm.get('role')?.invalid && userForm.get('role')?.touched">
              El rol es requerido
            </div>
          </div>

          <div class="form-group" *ngIf="false">
            <label for="password">Contraseña *</label>
            <input 
              type="password" 
              id="password"
              formControlName="password"
              class="form-control"
              [class.error]="userForm.get('password')?.invalid && userForm.get('password')?.touched">
            <div class="error-message" *ngIf="userForm.get('password')?.invalid && userForm.get('password')?.touched">
              <span *ngIf="userForm.get('password')?.errors?.['required']">La contraseña es requerida</span>
              <span *ngIf="userForm.get('password')?.errors?.['minlength']">La contraseña debe tener al menos 6 caracteres</span>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-outline" (click)="goBack()">
              Cancelar
            </button>
            <button 
              type="submit" 
              class="btn btn-primary" 
              [disabled]="userForm.invalid || loading">
              {{ loading ? 'Guardando...' : (isEdit ? 'Actualizar' : 'Crear') }}
            </button>
          </div>
        </form>

        <div *ngIf="error" class="error-alert">
          {{ error }}
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 800px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .form-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
    .form-group { margin-bottom: 20px; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
    label { display: block; margin-bottom: 5px; font-weight: 500; color: #333; }
    .form-control { 
      width: 100%; 
      padding: 10px; 
      border: 1px solid #ddd; 
      border-radius: 4px; 
      font-size: 14px;
      transition: border-color 0.2s;
    }
    .form-control:focus { 
      outline: none; 
      border-color: #007bff; 
      box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
    }
    .form-control.error { border-color: #dc3545; }
    .error-message { 
      color: #dc3545; 
      font-size: 12px; 
      margin-top: 5px; 
    }
    .form-actions { 
      display: flex; 
      gap: 15px; 
      justify-content: flex-end; 
      margin-top: 30px; 
      padding-top: 20px;
      border-top: 1px solid #eee;
    }
    .btn { 
      padding: 10px 20px; 
      border: 1px solid #ddd; 
      border-radius: 4px; 
      cursor: pointer; 
      font-size: 14px;
      transition: all 0.2s;
    }
    .btn-primary { 
      background: #007bff; 
      color: white; 
      border-color: #007bff; 
    }
    .btn-primary:hover:not(:disabled) { 
      background: #0056b3; 
      border-color: #0056b3;
    }
    .btn-primary:disabled { 
      opacity: 0.6; 
      cursor: not-allowed; 
    }
    .btn-outline { 
      background: white; 
      color: #6c757d;
    }
    .btn-outline:hover { 
      background: #f8f9fa; 
    }
    .error-alert { 
      background: #f8d7da; 
      color: #721c24; 
      padding: 15px; 
      border-radius: 4px; 
      margin-top: 20px;
      border: 1px solid #f5c6cb;
    }
  `]
})
export class UserFormComponent implements OnInit {
  userForm: FormGroup;
  loading = false;
  error: string | null = null;
  isEdit = false;
  userId: number | null = null;

  roleOptions = [
    { value: UserRole.USER, label: 'Usuario' },
    { value: UserRole.ORGANIZER, label: 'Organizador' },
    { value: UserRole.SUBADMIN, label: 'Subadministrador' }
  ];

  constructor(
    private fb: FormBuilder,
    private usersService: UsersService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.userForm = this.createForm();
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.userId = parseInt(id);
      this.loadUser();
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      role: ['', Validators.required]
    });
  }

  loadUser() {
    if (!this.userId) return;

    this.loading = true;
    this.usersService.getUser(this.userId).subscribe({
      next: (user) => {
        this.userForm.patchValue({
          email: user.email,
          fullName: user.firstName + ' ' + user.lastName,
          role: user.role
        });
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar el usuario';
        this.loading = false;
        console.error('Error loading user:', error);
      }
    });
  }

  onSubmit() {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    const formValue = this.userForm.value;
    const userData = {
      email: formValue.email,
      fullName: formValue.fullName,
      role: formValue.role
    };

    if (this.isEdit && this.userId) {
      // Para edición, no enviamos la contraseña si está vacía
      const updateData = { ...formValue };
      if (!updateData.password) {
        delete updateData.password;
      }

      this.usersService.updateUser(this.userId, userData).subscribe({
        next: () => {
          this.router.navigate(['/users']);
        },
        error: (error) => {
          this.error = 'Error al actualizar el usuario';
          this.loading = false;
          console.error('Error updating user:', error);
        }
      });
    } else {
      this.usersService.createUser(userData).subscribe({
        next: () => {
          this.router.navigate(['/users']);
        },
        error: (error) => {
          this.error = 'Error al crear el usuario';
          this.loading = false;
          console.error('Error creating user:', error);
        }
      });
    }
  }

  goBack() {
    this.router.navigate(['/users']);
  }
}