import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { TicketSaleStage, TicketSaleStageRequest } from '../../../core/models/ticket-stage.models';

@Injectable({
  providedIn: 'root'
})
export class TicketStagesService extends HttpBaseService {

  getStages(tournamentId: number): Observable<TicketSaleStage[]> {
    return this.get<TicketSaleStage[]>(`/tournaments/${tournamentId}/stages`);
  }

  createStage(tournamentId: number, stage: TicketSaleStageRequest): Observable<TicketSaleStage> {
    return this.post<TicketSaleStage>(`/tournaments/${tournamentId}/stages`, stage);
  }

  updateStage(tournamentId: number, stageId: number, stage: TicketSaleStageRequest): Observable<TicketSaleStage> {
    return this.put<TicketSaleStage>(`/tournaments/${tournamentId}/stages/${stageId}`, stage);
  }
}