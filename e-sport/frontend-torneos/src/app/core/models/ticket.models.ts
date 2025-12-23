export interface TicketOrder {
  id: number;
  tournamentId: number;
  userId: number;
  quantity: number;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
  tickets: Ticket[];
}

export interface TicketOrderRequest {
  quantity: number;
  stageId?: number;
}

export interface Ticket {
  id: number;
  accessCode: string;
  orderId: number;
  tournamentId: number;
  stageId: number;
  price: number;
  status: TicketStatus;
  isValidated: boolean;
  validatedAt?: string;
  createdAt: string;
}

export interface CartItem {
  stageId: number;
  stageName: string;
  price: number;
  quantity: number;
  maxQuantity: number;
}

export interface OrderSummary {
  items: CartItem[];
  totalQuantity: number;
  totalAmount: number;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

export enum TicketStatus {
  ACTIVE = 'ACTIVE',
  USED = 'USED',
  CANCELLED = 'CANCELLED',
  EXPIRED = 'EXPIRED'
}