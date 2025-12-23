import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpBaseService } from './http-base.service';
import { PaginatedResponse } from '../models/api.models';
import { Category } from '../models/tournament.models';

@Injectable({
  providedIn: 'root'
})
export class CategoriesService extends HttpBaseService {
  private readonly endpoint = '/categories';

  getCategories(): Observable<PaginatedResponse<Category>> {
    return this.get<PaginatedResponse<Category>>(this.endpoint);
  }
}