import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { PaginatedResponse } from '../../../core/models/api.models';
import { Tournament, ITournament, TournamentRequest, TournamentFilters, SubAdmin, AssignSubAdminRequest } from '../../../core/models/tournament.models';

@Injectable({
  providedIn: 'root'
})
export class TournamentsService extends HttpBaseService {

  private readonly endpoint = '/tournaments';

  getTournaments(page: number = 0, size: number = 10, filters?: TournamentFilters): Observable<PaginatedResponse<Tournament>> {
    // Usar endpoint temporal simple
    return this.get<any>('/simple/tournaments')
      .pipe(
        map(response => ({
          ...response,
          content: response.content.map((t: any) => new Tournament({
            id: t.id,
            name: t.name,
            description: t.description,
            status: t.status,
            isPaid: t.is_paid,
            maxFreeCapacity: t.max_free_capacity,
            startDateTime: t.start_date_time,
            endDateTime: t.end_date_time,
            categoryId: t.category_id,
            gameTypeId: t.game_type_id,
            createdAt: t.start_date_time, // Temporal
            updatedAt: t.start_date_time, // Temporal
            category: t.category_name ? { 
              id: t.category_id, 
              name: t.category_name, 
              active: true 
            } : undefined,
            gameType: t.game_type_name ? { 
              id: t.game_type_id, 
              name: t.game_type_name, 
              active: true 
            } : undefined
          }))
        }))
      );
  }

  getTournament(id: number | string): Observable<Tournament> {
    return this.get<any>(`/simple/tournaments/${id}`)
      .pipe(map(data => new Tournament({
        id: data.id,
        name: data.name,
        description: data.description,
        status: data.status,
        isPaid: data.is_paid,
        maxFreeCapacity: data.max_free_capacity,
        startDateTime: data.start_date_time,
        endDateTime: data.end_date_time,
        categoryId: data.category_id,
        gameTypeId: data.game_type_id,
        createdAt: data.start_date_time,
        updatedAt: data.start_date_time,
        category: data.category_name ? { 
          id: data.category_id, 
          name: data.category_name, 
          active: true 
        } : undefined,
        gameType: data.game_type_name ? { 
          id: data.game_type_id, 
          name: data.game_type_name, 
          active: true 
        } : undefined
      })));
  }

  createTournament(tournament: TournamentRequest): Observable<Tournament> {
    // Usar endpoint temporal simple
    return this.post<any>('/simple/tournaments', tournament)
      .pipe(map(data => new Tournament(data)));
  }

  updateTournament(id: number | string, tournament: TournamentRequest): Observable<Tournament> {
    return this.put<ITournament>(`${this.endpoint}/${id}`, tournament)
      .pipe(map(data => new Tournament(data)));
  }

  publishTournament(id: number | string): Observable<Tournament> {
    return this.put<any>(`/simple/tournaments/${id}/publish`, {})
      .pipe(map(data => new Tournament({
        id: data.id,
        name: data.name,
        description: data.description,
        status: data.status,
        isPaid: data.is_paid,
        maxFreeCapacity: data.max_free_capacity,
        startDateTime: data.start_date_time,
        endDateTime: data.end_date_time,
        categoryId: data.category_id,
        gameTypeId: data.game_type_id,
        createdAt: data.start_date_time,
        updatedAt: data.start_date_time
      })));
  }

  // SubAdmin management
  getSubAdmins(tournamentId: number | string): Observable<SubAdmin[]> {
    return this.get<SubAdmin[]>(`${this.endpoint}/${tournamentId}/subadmins`);
  }

  assignSubAdmin(tournamentId: number | string, request: AssignSubAdminRequest): Observable<void> {
    return this.post<void>(`${this.endpoint}/${tournamentId}/subadmins`, request);
  }

  removeSubAdmin(tournamentId: number | string, subAdminId: number): Observable<void> {
    return this.delete<void>(`${this.endpoint}/${tournamentId}/subadmins/${subAdminId}`);
  }
}