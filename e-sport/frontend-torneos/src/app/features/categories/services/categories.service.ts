import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { Category, CreateCategoryRequest, UpdateCategoryRequest } from '../../../core/models/masters.models';

@Injectable({
  providedIn: 'root'
})
export class CategoriesService extends HttpBaseService {

  getCategories(): Observable<Category[]> {
    return this.get<Category[]>('/categories/simple');
  }

  getCategory(id: string): Observable<Category> {
    return this.get<Category>(`/categories/${id}`);
  }

  createCategory(request: CreateCategoryRequest): Observable<Category> {
    return this.post<Category>('/categories', request);
  }

  updateCategory(id: string, request: UpdateCategoryRequest): Observable<Category> {
    return this.put<Category>(`/categories/${id}`, request);
  }
}