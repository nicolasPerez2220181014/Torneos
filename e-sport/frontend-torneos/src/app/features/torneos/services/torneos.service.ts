import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { PaginatedResponse } from '../../../core/models/api.models';

export interface Tournament {
  id: string;
  organizerId: string;
  categoryId: string;
  gameTypeId: string;
  name: string;
  description: string;
  isPaid: boolean;
  maxFreeCapacity?: number;
  startDateTime: string;
  endDateTime: string;
  status: 'DRAFT' | 'PUBLISHED' | 'FINISHED' | 'CANCELLED';
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class TorneosService extends HttpBaseService {

  getTournaments(): Observable<PaginatedResponse<Tournament>> {
    return this.get<PaginatedResponse<Tournament>>('/tournaments');
  }

  getTournament(id: string): Observable<Tournament> {
    return this.get<Tournament>(`/tournaments/${id}`);
  }
}