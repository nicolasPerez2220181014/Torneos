import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { TournamentRequest, Tournament, ITournament } from '../models/tournament.models';

@Injectable({
  providedIn: 'root'
})
export class TournamentTestService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  createTournamentWithMockAuth(tournament: TournamentRequest): Observable<Tournament> {
    const headers = {
      'X-USER-ID': '1',
      'Content-Type': 'application/json'
    };

    return this.http.post<ITournament>(`${this.apiUrl}/tournaments`, tournament, { headers })
      .pipe(map(data => new Tournament(data)));
  }
}