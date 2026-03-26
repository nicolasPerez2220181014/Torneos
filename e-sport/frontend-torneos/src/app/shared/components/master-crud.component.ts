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
    .master-crud { max-width: 1200px; margin: 0 auto; padding: var(--sp-xl); }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--sp-xl); }
    .header h2 { font-size: 1.4rem; }
    .form-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      padding: var(--sp-xl);
      border-radius: var(--radius-lg);
      margin-bottom: var(--sp-xl);
    }
    .form-card h3 { font-size: 1rem; margin: 0 0 var(--sp-lg) 0; }
    .form-group { margin-bottom: var(--sp-lg); }
    .form-actions { display: flex; gap: var(--sp-sm); }
    .btn-primary {
      background: var(--accent);
      color: #fff;
      border: none;
      padding: 8px 16px;
      border-radius: var(--radius-md);
      cursor: pointer;
      font-family: var(--font-body);
      font-size: 0.85rem;
      font-weight: 500;
      transition: all 0.15s var(--ease);
    }
    .btn-primary:hover { background: var(--accent-hover); }
    .btn-primary:disabled { opacity: 0.4; cursor: not-allowed; }
    .btn-secondary {
      background: var(--bg-secondary);
      color: var(--text-secondary);
      border: 1px solid var(--border);
      padding: 8px 16px;
      border-radius: var(--radius-md);
      cursor: pointer;
      font-family: var(--font-body);
      font-size: 0.85rem;
      transition: all 0.15s var(--ease);
    }
    .btn-secondary:hover { border-color: var(--border-hover); color: var(--text-primary); }
    .btn-edit {
      background: transparent;
      color: var(--accent);
      border: 1px solid var(--accent);
      padding: 4px 12px;
      border-radius: var(--radius-sm);
      cursor: pointer;
      font-size: 0.75rem;
      font-family: var(--font-body);
      font-weight: 500;
      transition: all 0.15s var(--ease);
    }
    .btn-edit:hover { background: var(--accent-soft); }
    .btn-edit:disabled { opacity: 0.4; cursor: not-allowed; }
    .items-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: var(--sp-md);
    }
    .item-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      padding: var(--sp-lg);
      display: flex;
      justify-content: space-between;
      align-items: center;
      transition: all 0.2s var(--ease);
    }
    .item-card:hover { border-color: var(--border-hover); }
    .item-content h4 { margin: 0 0 var(--sp-xs) 0; font-size: 0.95rem; color: var(--text-primary); }
    .status {
      padding: 2px 8px;
      border-radius: var(--radius-full);
      font-size: 0.65rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    .status.active { background: var(--success-soft); color: var(--success); }
    .status.inactive { background: var(--danger-soft); color: var(--danger); }
    @media (max-width: 768px) { .items-grid { grid-template-columns: 1fr; } }
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