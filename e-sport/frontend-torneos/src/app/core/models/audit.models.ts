export interface AuditLog {
  id: number;
  userId: number;
  action: string;
  entityType: string;
  entityId: number;
  details: string;
  ipAddress: string;
  userAgent: string;
  timestamp: string;
  user?: {
    email: string;
    firstName: string;
    lastName: string;
  };
}

export interface AuditFilters {
  userId?: number;
  action?: string;
  entityType?: string;
  startDate?: string;
  endDate?: string;
}

export interface DashboardMetrics {
  totalTournaments: number;
  activeTournaments: number;
  totalUsers: number;
  totalTicketsSold: number;
  totalRevenue: number;
  activeStreams: number;
  totalViews: number;
  recentActivity: ActivitySummary[];
}

export interface ActivitySummary {
  type: ActivityType;
  count: number;
  label: string;
  trend: number; // percentage change
}

export enum ActivityType {
  TOURNAMENTS_CREATED = 'TOURNAMENTS_CREATED',
  TICKETS_SOLD = 'TICKETS_SOLD',
  USERS_REGISTERED = 'USERS_REGISTERED',
  STREAMS_STARTED = 'STREAMS_STARTED'
}