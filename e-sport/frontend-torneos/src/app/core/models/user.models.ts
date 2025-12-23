export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string;
}

export interface UserRequest {
  email: string;
  fullName: string;
  role: UserRole;
}

export interface UserFilters {
  email?: string;
  firstName?: string;
  lastName?: string;
  role?: UserRole;
}

export enum UserRole {
  USER = 'USER',
  ORGANIZER = 'ORGANIZER', 
  SUBADMIN = 'SUBADMIN'
}

export interface UserSearchResult {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
}