import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpBaseService } from '../../../core/services/http-base.service';
import { TicketOrder, TicketOrderRequest, Ticket } from '../../../core/models/ticket.models';

@Injectable({
  providedIn: 'root'
})
export class TicketsService extends HttpBaseService {

  createOrder(tournamentId: number | string, orderRequest: TicketOrderRequest): Observable<TicketOrder> {
    return this.post<TicketOrder>(`/tournaments/${tournamentId}/orders`, orderRequest);
  }

  getOrder(orderId: number): Observable<TicketOrder> {
    return this.get<TicketOrder>(`/orders/${orderId}`);
  }

  getTournamentTickets(tournamentId: number | string): Observable<Ticket[]> {
    return this.get<Ticket[]>(`/tournaments/${tournamentId}/tickets`);
  }

  getTicketByCode(accessCode: string): Observable<Ticket> {
    return this.get<Ticket>(`/tickets/${accessCode}`);
  }

  validateTicket(accessCode: string): Observable<Ticket> {
    return this.post<Ticket>(`/tickets/${accessCode}/validate`, {});
  }
}