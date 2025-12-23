import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { GameType, CreateGameTypeRequest, UpdateGameTypeRequest } from '../../../core/models/masters.models';

@Injectable({
  providedIn: 'root'
})
export class GameTypesService extends HttpBaseService {

  getGameTypes(): Observable<GameType[]> {
    return this.get<GameType[]>('/game-types/simple');
  }

  getGameType(id: string): Observable<GameType> {
    return this.get<GameType>(`/game-types/${id}`);
  }

  createGameType(request: CreateGameTypeRequest): Observable<GameType> {
    return this.post<GameType>('/game-types', request);
  }

  updateGameType(id: string, request: UpdateGameTypeRequest): Observable<GameType> {
    return this.put<GameType>(`/game-types/${id}`, request);
  }
}