export interface TicketSaleStage {
  id: number;
  name: string;
  description: string;
  price: number;
  capacity: number;
  soldTickets: number;
  startDate: string;
  endDate: string;
  isActive: boolean;
  tournamentId: number;
  createdAt: string;
  updatedAt: string;
}

export interface TicketSaleStageRequest {
  name: string;
  description: string;
  price: number;
  capacity: number;
  startDate: string;
  endDate: string;
}

export interface TicketSaleStageFilters {
  name?: string;
  isActive?: boolean;
  minPrice?: number;
  maxPrice?: number;
}

export enum StageStatus {
  UPCOMING = 'UPCOMING',
  ACTIVE = 'ACTIVE',
  EXPIRED = 'EXPIRED',
  SOLD_OUT = 'SOLD_OUT'
}