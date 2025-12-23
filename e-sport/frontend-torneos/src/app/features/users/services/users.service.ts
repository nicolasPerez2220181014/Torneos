import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { PaginatedResponse } from '../../../core/models/api.models';
import { User, UserRequest, UserFilters, UserSearchResult } from '../../../core/models/user.models';

@Injectable({
  providedIn: 'root'
})
export class UsersService extends HttpBaseService {

  private readonly endpoint = '/users';

  getUsers(page: number = 0, size: number = 10, filters?: UserFilters): Observable<PaginatedResponse<User>> {
    let params: any = { page, size };
    
    if (filters) {
      if (filters.email) params.email = filters.email;
      if (filters.firstName) params.firstName = filters.firstName;
      if (filters.lastName) params.lastName = filters.lastName;
      if (filters.role) params.role = filters.role;
    }

    return this.get<PaginatedResponse<User>>(this.endpoint, new HttpParams({ fromObject: params }));
  }

  getUser(id: number): Observable<User> {
    return this.get<User>(`${this.endpoint}/${id}`);
  }

  getUserByEmail(email: string): Observable<User> {
    return this.get<User>(`${this.endpoint}/email/${email}`);
  }

  createUser(user: UserRequest): Observable<User> {
    return this.post<User>(this.endpoint, user);
  }

  updateUser(id: number, user: Partial<UserRequest>): Observable<User> {
    return this.put<User>(`${this.endpoint}/${id}`, user);
  }

  searchUsers(query: string): Observable<UserSearchResult[]> {
    return this.getUsers(0, 50, { 
      email: query.includes('@') ? query : undefined,
      firstName: !query.includes('@') ? query : undefined 
    }).pipe(
      map(response => response.content.map(user => ({
        id: user.id,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        fullName: `${user.firstName} ${user.lastName}`
      })))
    );
  }
}