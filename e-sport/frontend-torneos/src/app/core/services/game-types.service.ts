import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpBaseService } from './http-base.service';
import { PaginatedResponse } from '../models/api.models';
import { GameType } from '../models/tournament.models';

@Injectable({
  providedIn: 'root'
})
export class GameTypesService extends HttpBaseService {
  private readonly endpoint = '/game-types';

  getGameTypes(): Observable<PaginatedResponse<GameType>> {
    return this.get<PaginatedResponse<GameType>>(this.endpoint);
  }
}