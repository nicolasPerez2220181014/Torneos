import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface MasterItem {
  id: string;
  name: string;
  active: boolean;
}

@Component({
  selector: 'app-master-crud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="master-crud">
      <div class="header">
        <h2>{{ title }}</h2>
        <button 
          class="btn-primary" 
          (click)="showCreateForm = true"
          [disabled]="loading">
          Crear {{ entityName }}
        </button>
      </div>

      <!-- Create Form -->
      <div *ngIf="showCreateForm" class="form-card">
        <h3>Crear {{ entityName }}</h3>
        <form (ngSubmit)="onCreate()" #createForm="ngForm">
          <div class="form-group">
            <label>Nombre:</label>
            <input 
              type="text" 
              [(ngModel)]="newItemName" 
              name="name"
              required
              class="form-control">
          </div>
          <div class="form-actions">
            <button type="submit" [disabled]="!createForm.valid || loading" class="btn-primary">
              {{ loading ? 'Creando...' : 'Crear' }}
            </button>
            <button type="button" (click)="cancelCreate()" class="btn-secondary">
              Cancelar
            </button>
          </div>
        </form>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="loading">
        Cargando {{ title.toLowerCase() }}...
      </div>

      <!-- Error -->
      <div *ngIf="error" class="error">
        Error: {{ error }}
      </div>

      <!-- Items List -->
      <div *ngIf="items && !loading" class="items-grid">
        <div *ngFor="let item of items" class="item-card">
          <div class="item-content">
            <h4>{{ item.name }}</h4>
            <span class="status" [class.active]="item.active" [class.inactive]="!item.active">
              {{ item.active ? 'Activo' : 'Inactivo' }}
            </span>
          </div>
          <div class="item-actions">
            <button 
              class="btn-edit" 
              (click)="startEdit(item)"
              [disabled]="loading">
              Editar
            </button>
          </div>
        </div>
      </div>

      <!-- Edit Form -->
      <div *ngIf="editingItem" class="form-card">
        <h3>Editar {{ entityName }}</h3>
        <form (ngSubmit)="onUpdate()" #editForm="ngForm">
          <div class="form-group">
            <label>Nombre:</label>
            <input 
              type="text" 
              [(ngModel)]="editItemName" 
              name="editName"
              required
              class="form-control">
          </div>
          <div class="form-actions">
            <button type="submit" [disabled]="!editForm.valid || loading" class="btn-primary">
              {{ loading ? 'Actualizando...' : 'Actualizar' }}
            </button>
            <button type="button" (click)="cancelEdit()" class="btn-secondary">
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .master-crud {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }
    .form-card {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 20px;
    }
    .form-group {
      margin-bottom: 15px;
    }
    .form-control {
      width: 100%;
      padding: 8px 12px;
      border: 1px solid #ddd;
      border-radius: 4px;
      box-sizing: border-box;
    }
    .form-actions {
      display: flex;
      gap: 10px;
    }
    .btn-primary {
      background: #007bff;
      color: white;
      border: none;
      padding: 8px 16px;
      border-radius: 4px;
      cursor: pointer;
    }
    .btn-primary:disabled {
      background: #6c757d;
      cursor: not-allowed;
    }
    .btn-secondary {
      background: #6c757d;
      color: white;
      border: none;
      padding: 8px 16px;
      border-radius: 4px;
      cursor: pointer;
    }
    .btn-edit {
      background: #28a745;
      color: white;
      border: none;
      padding: 4px 8px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 12px;
    }
    .loading {
      color: #007bff;
      font-weight: bold;
      text-align: center;
      padding: 20px;
    }
    .error {
      color: #dc3545;
      background: #f8d7da;
      padding: 10px;
      border-radius: 4px;
      margin-bottom: 20px;
    }
    .items-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 15px;
    }
    .item-card {
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 15px;
      background: white;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .item-content h4 {
      margin: 0 0 5px 0;
    }
    .status {
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: bold;
    }
    .status.active {
      background: #d4edda;
      color: #155724;
    }
    .status.inactive {
      background: #f8d7da;
      color: #721c24;
    }
  `]
})
export class MasterCrudComponent {
  @Input() title = '';
  @Input() entityName = '';
  @Input() items: MasterItem[] | null = null;
  @Input() loading = false;
  @Input() error: string | null = null;

  @Output() create = new EventEmitter<string>();
  @Output() update = new EventEmitter<{id: string, name: string}>();
  @Output() refresh = new EventEmitter<void>();

  showCreateForm = false;
  newItemName = '';
  editingItem: MasterItem | null = null;
  editItemName = '';

  onCreate(): void {
    if (this.newItemName.trim()) {
      this.create.emit(this.newItemName.trim());
    }
  }

  onUpdate(): void {
    if (this.editingItem && this.editItemName.trim()) {
      this.update.emit({
        id: this.editingItem.id,
        name: this.editItemName.trim()
      });
    }
  }

  startEdit(item: MasterItem): void {
    this.editingItem = item;
    this.editItemName = item.name;
  }

  cancelCreate(): void {
    this.showCreateForm = false;
    this.newItemName = '';
  }

  cancelEdit(): void {
    this.editingItem = null;
    this.editItemName = '';
  }

  resetForms(): void {
    this.showCreateForm = false;
    this.newItemName = '';
    this.editingItem = null;
    this.editItemName = '';
  }
}