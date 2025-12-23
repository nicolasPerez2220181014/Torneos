export interface ITournament {
  id: string;
  name: string;
  description: string;
  startDateTime: string;
  endDateTime: string;
  maxFreeCapacity: number;
  isPaid: boolean;
  status: TournamentStatus;
  categoryId: string;
  gameTypeId: string;
  category?: Category;
  gameType?: GameType;
  createdAt: string;
  updatedAt: string;
}

export class Tournament implements ITournament {
  id!: string;
  name!: string;
  description!: string;
  startDateTime!: string;
  endDateTime!: string;
  maxFreeCapacity!: number;
  isPaid!: boolean;
  status!: TournamentStatus;
  categoryId!: string;
  gameTypeId!: string;
  category?: Category;
  gameType?: GameType;
  createdAt!: string;
  updatedAt!: string;

  constructor(data: ITournament) {
    Object.assign(this, data);
  }

  get startDate(): string {
    return this.startDateTime;
  }

  get endDate(): string {
    return this.endDateTime;
  }

  get maxParticipants(): number {
    return this.maxFreeCapacity;
  }

  get registrationStartDate(): string {
    return this.startDateTime;
  }

  get registrationEndDate(): string {
    return this.endDateTime;
  }
}

export interface TournamentRequest {
  name: string;
  description: string;
  startDateTime: string;
  endDateTime: string;
  maxFreeCapacity: number;
  isPaid: boolean;
  categoryId: string;
  gameTypeId: string;
}

export interface TournamentFilters {
  name?: string;
  status?: TournamentStatus;
  categoryId?: string;
  gameTypeId?: string;
  startDate?: string;
  endDate?: string;
}

export enum TournamentStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  REGISTRATION_OPEN = 'REGISTRATION_OPEN',
  REGISTRATION_CLOSED = 'REGISTRATION_CLOSED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface Category {
  id: string;
  name: string;
  active: boolean;
}

export interface GameType {
  id: string;
  name: string;
  active: boolean;
}

export interface SubAdmin {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  assignedAt: string;
}

export interface AssignSubAdminRequest {
  subAdminId: number;
}