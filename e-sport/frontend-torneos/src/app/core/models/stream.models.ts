export interface StreamAccess {
  id: number;
  tournamentId: number;
  userId: number;
  accessType: StreamAccessType;
  isActive: boolean;
  expiresAt?: string;
  createdAt: string;
  user?: {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
  };
}

export interface StreamAccessRequest {
  accessType: StreamAccessType;
}

export interface StreamStatus {
  tournamentId: number;
  isLive: boolean;
  streamUrl?: string;
  isBlocked: boolean;
  viewerCount: number;
  lastUpdated: string;
}

export interface StreamUrlUpdate {
  streamUrl: string;
}

export enum StreamAccessType {
  FREE = 'FREE',
  PAID = 'PAID',
  VIP = 'VIP'
}