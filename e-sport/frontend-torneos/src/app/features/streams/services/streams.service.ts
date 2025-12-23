import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { StreamAccess, StreamAccessRequest, StreamStatus, StreamUrlUpdate } from '../../../core/models/stream.models';

@Injectable({
  providedIn: 'root'
})
export class StreamsService extends HttpBaseService {

  // Stream Access Management
  requestAccess(tournamentId: number | string, request: StreamAccessRequest): Observable<StreamAccess> {
    return this.post<StreamAccess>(`/tournaments/${tournamentId}/access`, request);
  }

  getAccesses(tournamentId: number | string): Observable<StreamAccess[]> {
    return this.get<StreamAccess[]>(`/tournaments/${tournamentId}/access`);
  }

  // Stream Control (Admin)
  updateStreamUrl(tournamentId: number | string, update: StreamUrlUpdate): Observable<void> {
    return this.put<void>(`/tournaments/${tournamentId}/stream/url`, update);
  }

  blockStream(tournamentId: number | string): Observable<void> {
    return this.post<void>(`/tournaments/${tournamentId}/stream/block`, {});
  }

  unblockStream(tournamentId: number | string): Observable<void> {
    return this.post<void>(`/tournaments/${tournamentId}/stream/unblock`, {});
  }

  getStreamStatus(tournamentId: number | string): Observable<StreamStatus> {
    return this.get<StreamStatus>(`/tournaments/${tournamentId}/stream/status`);
  }
}