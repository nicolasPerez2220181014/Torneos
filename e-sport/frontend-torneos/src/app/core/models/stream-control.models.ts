export interface StreamSettings {
  tournamentId: number;
  quality: StreamQuality;
  maxViewers: number;
  chatEnabled: boolean;
  moderationEnabled: boolean;
  recordingEnabled: boolean;
  autoStart: boolean;
  streamKey: string;
}

export interface StreamAnalytics {
  tournamentId: number;
  totalViews: number;
  peakViewers: number;
  averageViewTime: number;
  chatMessages: number;
  qualityDistribution: QualityStats[];
  viewersByHour: ViewerStats[];
}

export interface QualityStats {
  quality: StreamQuality;
  percentage: number;
  viewers: number;
}

export interface ViewerStats {
  hour: string;
  viewers: number;
}

export interface ChatMessage {
  id: number;
  userId: number;
  username: string;
  message: string;
  timestamp: string;
  isModerated: boolean;
}

export enum StreamQuality {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  ULTRA = 'ULTRA'
}