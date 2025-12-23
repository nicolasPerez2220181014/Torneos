import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/pages/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'tournaments',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/tournaments/pages/tournaments-list.component').then(m => m.TournamentsListComponent)
      },
      {
        path: 'create',
        loadComponent: () => import('./features/tournaments/pages/tournament-form.component').then(m => m.TournamentFormComponent)
      },
      {
        path: ':id',
        loadComponent: () => import('./features/tournaments/pages/tournament-detail.component').then(m => m.TournamentDetailComponent)
      },
      {
        path: ':id/edit',
        loadComponent: () => import('./features/tournaments/pages/tournament-form.component').then(m => m.TournamentFormComponent)
      }
    ]
  },
  {
    path: 'users',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/users/pages/users-list.component').then(m => m.UsersListComponent)
      },
      {
        path: 'create',
        loadComponent: () => import('./features/users/pages/user-form.component').then(m => m.UserFormComponent)
      },
      {
        path: ':id',
        loadComponent: () => import('./features/users/pages/user-form.component').then(m => m.UserFormComponent)
      },
      {
        path: ':id/edit',
        loadComponent: () => import('./features/users/pages/user-form.component').then(m => m.UserFormComponent)
      }
    ]
  },
  {
    path: 'tickets',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/tickets/pages/my-tickets.component').then(m => m.MyTicketsComponent)
      },
      {
        path: 'purchase/:tournamentId',
        loadComponent: () => import('./features/tickets/pages/ticket-purchase.component').then(m => m.TicketPurchaseComponent)
      }
    ]
  },
  {
    path: 'validation',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/ticket-validation/pages/ticket-validation.component').then(m => m.TicketValidationComponent)
      },
      {
        path: 'lookup',
        loadComponent: () => import('./features/ticket-validation/pages/ticket-lookup.component').then(m => m.TicketLookupComponent)
      },
      {
        path: ':tournamentId',
        loadComponent: () => import('./features/ticket-validation/pages/ticket-validation.component').then(m => m.TicketValidationComponent)
      }
    ]
  },
  {
    path: 'streams',
    canActivate: [AuthGuard],
    children: [
      {
        path: 'watch/:tournamentId',
        loadComponent: () => import('./features/streams/pages/stream-viewer.component').then(m => m.StreamViewerComponent)
      }
    ]
  },
  {
    path: 'dashboard',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/pages/executive-dashboard.component').then(m => m.ExecutiveDashboardComponent)
      },
      {
        path: 'audit',
        loadComponent: () => import('./features/dashboard/pages/audit-logs.component').then(m => m.AuditLogsComponent)
      }
    ]
  },
  {
    path: 'categories',
    loadComponent: () => import('./features/categories/pages/categories-list.component').then(m => m.CategoriesListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'game-types',
    loadComponent: () => import('./features/game-types/pages/game-types-list.component').then(m => m.GameTypesListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'test-tournament',
    loadComponent: () => import('./test-tournament.component').then(m => m.TestTournamentComponent)
  },
  {
    path: 'torneos',
    redirectTo: '/tournaments',
    pathMatch: 'full'
  },
  {
    path: '',
    redirectTo: '/tournaments',
    pathMatch: 'full'
  }
];
