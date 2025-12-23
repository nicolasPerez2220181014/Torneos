import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsersService } from '../../features/users/services/users.service';
import { UserSearchResult } from '../../core/models/user.models';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-user-selector',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="user-selector">
      <div class="search-container">
        <input 
          type="text" 
          placeholder="Buscar usuario por nombre o email..."
          [(ngModel)]="searchQuery"
          (input)="onSearchInput($event)"
          class="form-control search-input">
        
        <div *ngIf="searching" class="search-loading">
          Buscando...
        </div>
      </div>

      <div *ngIf="searchResults.length > 0" class="search-results">
        <div 
          *ngFor="let user of searchResults" 
          class="user-result"
          (click)="selectUser(user)">
          <div class="user-info">
            <strong>{{user.fullName}}</strong>
            <span class="email">{{user.email}}</span>
          </div>
          <button class="btn btn-sm btn-primary">
            {{buttonText}}
          </button>
        </div>
      </div>

      <div *ngIf="searchQuery && !searching && searchResults.length === 0" class="no-results">
        No se encontraron usuarios
      </div>
    </div>
  `,
  styles: [`
    .user-selector { position: relative; }
    .search-container { position: relative; }
    .search-input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
    .search-loading { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); color: #666; font-size: 12px; }
    .search-results { 
      position: absolute; 
      top: 100%; 
      left: 0; 
      right: 0; 
      background: white; 
      border: 1px solid #ddd; 
      border-top: none; 
      border-radius: 0 0 4px 4px; 
      max-height: 200px; 
      overflow-y: auto; 
      z-index: 1000; 
    }
    .user-result { 
      display: flex; 
      justify-content: space-between; 
      align-items: center; 
      padding: 10px; 
      border-bottom: 1px solid #eee; 
      cursor: pointer; 
    }
    .user-result:hover { background: #f5f5f5; }
    .user-result:last-child { border-bottom: none; }
    .user-info { display: flex; flex-direction: column; gap: 4px; }
    .user-info strong { color: #333; }
    .user-info .email { color: #666; font-size: 12px; }
    .no-results { 
      padding: 20px; 
      text-align: center; 
      color: #999; 
      border: 1px solid #ddd; 
      border-top: none; 
      border-radius: 0 0 4px 4px; 
      background: white; 
    }
    .btn { padding: 6px 12px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-sm { padding: 4px 8px; font-size: 12px; }
  `]
})
export class UserSelectorComponent {
  @Input() buttonText = 'Seleccionar';
  @Output() userSelected = new EventEmitter<UserSearchResult>();

  searchQuery = '';
  searchResults: UserSearchResult[] = [];
  searching = false;

  private searchSubject = new Subject<string>();

  constructor(private usersService: UsersService) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (query.length < 2) {
          return of([]);
        }
        this.searching = true;
        return this.usersService.searchUsers(query);
      })
    ).subscribe({
      next: (results) => {
        this.searchResults = results;
        this.searching = false;
      },
      error: (error) => {
        console.error('Error searching users:', error);
        this.searchResults = [];
        this.searching = false;
      }
    });
  }

  onSearchInput(event: any) {
    const query = event.target.value;
    this.searchQuery = query;
    this.searchSubject.next(query);
  }

  selectUser(user: UserSearchResult) {
    this.userSelected.emit(user);
    this.searchQuery = '';
    this.searchResults = [];
  }
}