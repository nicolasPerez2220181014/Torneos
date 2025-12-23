import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MasterCrudComponent, MasterItem } from '../../../shared/components/master-crud.component';
import { CategoriesService } from '../services/categories.service';
import { Category } from '../../../core/models/masters.models';

@Component({
  selector: 'app-categories-list',
  standalone: true,
  imports: [CommonModule, MasterCrudComponent],
  template: `
    <app-master-crud
      title="Categorías"
      entityName="Categoría"
      [items]="categories"
      [loading]="loading"
      [error]="error"
      (create)="onCreateCategory($event)"
      (update)="onUpdateCategory($event)"
      (refresh)="loadCategories()">
    </app-master-crud>
  `
})
export class CategoriesListComponent implements OnInit {
  @ViewChild(MasterCrudComponent) masterCrud!: MasterCrudComponent;

  categories: MasterItem[] | null = null;
  loading = false;
  error: string | null = null;

  constructor(private categoriesService: CategoriesService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.error = null;

    this.categoriesService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories.map(cat => ({
          id: cat.id,
          name: cat.name,
          active: cat.active
        }));
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  onCreateCategory(name: string): void {
    this.loading = true;
    this.error = null;

    this.categoriesService.createCategory({ name }).subscribe({
      next: () => {
        this.loading = false;
        this.masterCrud.resetForms();
        this.loadCategories();
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  onUpdateCategory(data: {id: string, name: string}): void {
    this.loading = true;
    this.error = null;

    this.categoriesService.updateCategory(data.id, { name: data.name }).subscribe({
      next: () => {
        this.loading = false;
        this.masterCrud.resetForms();
        this.loadCategories();
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }
}