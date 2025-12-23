# Frontend Overview - Plataforma de Torneos E-Sport

## Información General
- **Tecnología**: Angular 17 + TypeScript 5.4
- **Arquitectura**: Modular por características (Feature-based)
- **UI Framework**: Bootstrap 5 + SCSS
- **Puerto**: 4200
- **URL**: http://localhost:4200

## 1. Arquitectura del Frontend

### 1.1 Visión Arquitectónica
El frontend implementa una **arquitectura modular por características** con separación clara entre módulos core, features y shared, siguiendo las mejores prácticas de Angular y principios de Clean Architecture.

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Components    │  │     Pages       │  │   Guards    │ │
│  │   (UI Logic)    │  │   (Routing)     │  │ (Security)  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                         │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │    Services     │  │  Interceptors   │  │   Models    │ │
│  │ (Business Logic)│  │ (HTTP Handling) │  │ (Data Types)│ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  HTTP Client    │  │   Local Storage │  │   Router    │ │
│  │ (API Calls)     │  │   (Persistence) │  │(Navigation) │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Estructura de Módulos
```
src/app/
├── core/                    # Módulo central (singleton)
│   ├── components/          # Componentes centrales (navbar)
│   ├── guards/              # Guards de autenticación
│   ├── interceptors/        # Interceptores HTTP
│   ├── models/              # Modelos TypeScript
│   ├── services/            # Servicios compartidos
│   └── utils/               # Utilidades
├── features/                # Módulos por característica
│   ├── auth/               # Autenticación
│   ├── tournaments/        # Gestión de torneos
│   ├── tickets/            # Gestión de tickets
│   ├── users/              # Gestión de usuarios
│   ├── dashboard/          # Panel de control
│   └── streams/            # Streaming
├── shared/                 # Componentes compartidos
│   └── components/         # Componentes reutilizables
├── styles/                 # Estilos globales
└── environments/           # Configuraciones de entorno
```

## 2. Módulo Core (Servicios Centrales)

### 2.1 Servicios de Autenticación
```typescript
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private router: Router
  ) {
    this.loadCurrentUser();
  }

  login(email: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', { email }).pipe(
      tap(response => {
        this.tokenService.setTokens(response.accessToken, response.refreshToken);
        this.loadCurrentUser();
      })
    );
  }

  logout(): void {
    this.tokenService.clearTokens();
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  private loadCurrentUser(): void {
    const token = this.tokenService.getAccessToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        // Load user details from token or API
        this.loadUserDetails(payload.sub);
      } catch (error) {
        console.error('Error loading user from token:', error);
        this.logout();
      }
    }
  }
}
```

### 2.2 Token Service
```typescript
@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';

  setTokens(accessToken: string, refreshToken: string): void {
    sessionStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    sessionStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  getAccessToken(): string | null {
    return sessionStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return sessionStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  clearTokens(): void {
    sessionStorage.removeItem(this.ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }

  isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // Convert to milliseconds
      return Date.now() >= exp;
    } catch {
      return true;
    }
  }
}
```

### 2.3 HTTP Base Service
```typescript
@Injectable({
  providedIn: 'root'
})
export class HttpBaseService {
  protected baseUrl = environment.apiUrl;

  constructor(protected http: HttpClient) {}

  protected get<T>(endpoint: string, options?: any): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, options);
  }

  protected post<T>(endpoint: string, data: any, options?: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, data, options);
  }

  protected put<T>(endpoint: string, data: any, options?: any): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${endpoint}`, data, options);
  }

  protected delete<T>(endpoint: string, options?: any): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${endpoint}`, options);
  }

  protected buildQueryParams(params: any): HttpParams {
    let httpParams = new HttpParams();
    
    Object.keys(params).forEach(key => {
      if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
        httpParams = httpParams.set(key, params[key].toString());
      }
    });
    
    return httpParams;
  }
}
```

## 3. Interceptores HTTP

### 3.1 Auth Interceptor
```typescript
const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Skip adding token to auth endpoints
  if (req.url.includes('/auth/')) {
    return next(req);
  }
  
  const tokenService = inject(TokenService);
  const token = tokenService.getAccessToken();
  
  if (token) {
    let headers = req.headers.set('Authorization', `Bearer ${token}`);
    
    // Extract user ID from token and add X-USER-ID header
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload.sub) {
        headers = headers.set('X-USER-ID', payload.sub);
      }
    } catch (e) {
      console.error('Error parsing token for user ID:', e);
    }
    
    const authReq = req.clone({ headers });
    return next(authReq);
  }
  
  return next(req);
};
```

### 3.2 Error Interceptor
```typescript
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Error desconocido';
        
        if (error.error instanceof ErrorEvent) {
          // Client-side error
          errorMessage = `Error: ${error.error.message}`;
        } else {
          // Server-side error - RFC 7807 Problem Details format
          if (error.error && typeof error.error === 'object') {
            const apiError = error.error as ApiError;
            errorMessage = apiError.detail || apiError.title || `Error ${error.status}`;
          } else {
            errorMessage = `Error ${error.status}: ${error.message}`;
          }
        }

        console.error('HTTP Error:', {
          status: error.status,
          message: errorMessage,
          url: error.url,
          error: error.error
        });

        return throwError(() => ({
          status: error.status,
          message: errorMessage,
          originalError: error
        }));
      })
    );
  }
}
```

## 4. Guards de Seguridad

### 4.1 Auth Guard
```typescript
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(
    private tokenService: TokenService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree {
    
    const token = this.tokenService.getAccessToken();
    
    if (token && !this.tokenService.isTokenExpired(token)) {
      return true;
    }
    
    // Redirect to login with return URL
    return this.router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }
}
```

### 4.2 Role Guard (Futuro)
```typescript
@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as string[];
    const currentUser = this.authService.getCurrentUser();
    
    if (currentUser && requiredRoles.includes(currentUser.role)) {
      return true;
    }
    
    this.router.navigate(['/unauthorized']);
    return false;
  }
}
```

## 5. Modelos TypeScript

### 5.1 Tournament Models
```typescript
export interface ITournament {
  id: string;
  name: string;
  description?: string;
  status: TournamentStatus;
  isPaid: boolean;
  maxFreeCapacity?: number;
  startDateTime: string;
  endDateTime: string;
  categoryId: string;
  gameTypeId: string;
  createdAt: string;
  updatedAt: string;
  category?: Category;
  gameType?: GameType;
}

export class Tournament implements ITournament {
  id: string;
  name: string;
  description?: string;
  status: TournamentStatus;
  isPaid: boolean;
  maxFreeCapacity?: number;
  startDateTime: string;
  endDateTime: string;
  categoryId: string;
  gameTypeId: string;
  createdAt: string;
  updatedAt: string;
  category?: Category;
  gameType?: GameType;

  constructor(data: Partial<ITournament>) {
    this.id = data.id || '';
    this.name = data.name || '';
    this.description = data.description;
    this.status = data.status || TournamentStatus.DRAFT;
    this.isPaid = data.isPaid || false;
    this.maxFreeCapacity = data.maxFreeCapacity;
    this.startDateTime = data.startDateTime || '';
    this.endDateTime = data.endDateTime || '';
    this.categoryId = data.categoryId || '';
    this.gameTypeId = data.gameTypeId || '';
    this.createdAt = data.createdAt || '';
    this.updatedAt = data.updatedAt || '';
    this.category = data.category;
    this.gameType = data.gameType;
  }

  get isActive(): boolean {
    return this.status === TournamentStatus.PUBLISHED;
  }

  get canBeModified(): boolean {
    return this.status === TournamentStatus.DRAFT;
  }

  get hasStarted(): boolean {
    return new Date(this.startDateTime) <= new Date();
  }
}

export enum TournamentStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED'
}
```

### 5.2 User Models
```typescript
export interface IUser {
  id: string;
  email: string;
  fullName: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string;
}

export class User implements IUser {
  id: string;
  email: string;
  fullName: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string;

  constructor(data: Partial<IUser>) {
    this.id = data.id || '';
    this.email = data.email || '';
    this.fullName = data.fullName || '';
    this.role = data.role || UserRole.USER;
    this.createdAt = data.createdAt || '';
    this.updatedAt = data.updatedAt || '';
  }

  get isOrganizer(): boolean {
    return this.role === UserRole.ORGANIZER;
  }

  get isSubAdmin(): boolean {
    return this.role === UserRole.SUBADMIN;
  }

  get canCreateTournaments(): boolean {
    return this.isOrganizer;
  }
}

export enum UserRole {
  USER = 'USER',
  ORGANIZER = 'ORGANIZER',
  SUBADMIN = 'SUBADMIN'
}
```

## 6. Feature Modules

### 6.1 Tournaments Feature

#### TournamentsService
```typescript
@Injectable({
  providedIn: 'root'
})
export class TournamentsService extends HttpBaseService {
  private readonly endpoint = '/tournaments';

  getTournaments(page: number = 0, size: number = 10, filters?: TournamentFilters): Observable<PaginatedResponse<Tournament>> {
    const params = this.buildQueryParams({ page, size, ...filters });
    return this.get<PaginatedResponse<any>>(`${this.endpoint}`, { params })
      .pipe(
        map(response => ({
          ...response,
          content: response.content.map((t: any) => new Tournament(t))
        }))
      );
  }

  getTournament(id: string): Observable<Tournament> {
    return this.get<any>(`${this.endpoint}/${id}`)
      .pipe(map(data => new Tournament(data)));
  }

  createTournament(tournament: TournamentRequest): Observable<Tournament> {
    return this.post<any>(`${this.endpoint}`, tournament)
      .pipe(map(data => new Tournament(data)));
  }

  updateTournament(id: string, tournament: TournamentRequest): Observable<Tournament> {
    return this.put<any>(`${this.endpoint}/${id}`, tournament)
      .pipe(map(data => new Tournament(data)));
  }

  publishTournament(id: string): Observable<Tournament> {
    return this.post<any>(`${this.endpoint}/${id}/publish`, {})
      .pipe(map(data => new Tournament(data)));
  }
}
```

#### TournamentsListComponent
```typescript
@Component({
  selector: 'app-tournaments-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="container-fluid">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Torneos</h2>
        <button class="btn btn-primary" routerLink="/tournaments/create">
          <i class="fas fa-plus"></i> Crear Torneo
        </button>
      </div>

      <!-- Filters -->
      <div class="row mb-4">
        <div class="col-md-3">
          <select class="form-select" [(ngModel)]="filters.status" (change)="applyFilters()">
            <option value="">Todos los estados</option>
            <option value="DRAFT">Borrador</option>
            <option value="PUBLISHED">Publicado</option>
            <option value="FINISHED">Finalizado</option>
          </select>
        </div>
        <div class="col-md-3">
          <select class="form-select" [(ngModel)]="filters.isPaid" (change)="applyFilters()">
            <option value="">Todos los tipos</option>
            <option value="true">Pagados</option>
            <option value="false">Gratuitos</option>
          </select>
        </div>
      </div>

      <!-- Tournament Cards -->
      <div class="row">
        @for (tournament of tournaments$ | async; track tournament.id) {
          <div class="col-md-6 col-lg-4 mb-4">
            <div class="card h-100">
              <div class="card-body">
                <h5 class="card-title">{{ tournament.name }}</h5>
                <p class="card-text">{{ tournament.description }}</p>
                <div class="d-flex justify-content-between align-items-center">
                  <span class="badge" [class]="getStatusBadgeClass(tournament.status)">
                    {{ tournament.status }}
                  </span>
                  <span class="text-muted">
                    {{ tournament.isPaid ? 'Pagado' : 'Gratuito' }}
                  </span>
                </div>
              </div>
              <div class="card-footer">
                <div class="btn-group w-100">
                  <button class="btn btn-outline-primary" [routerLink]="['/tournaments', tournament.id]">
                    Ver
                  </button>
                  @if (tournament.canBeModified) {
                    <button class="btn btn-outline-secondary" [routerLink]="['/tournaments', tournament.id, 'edit']">
                      Editar
                    </button>
                  }
                  @if (tournament.status === 'DRAFT') {
                    <button class="btn btn-success" (click)="publishTournament(tournament.id)">
                      Publicar
                    </button>
                  }
                </div>
              </div>
            </div>
          </div>
        }
      </div>

      <!-- Pagination -->
      <nav aria-label="Tournament pagination">
        <ul class="pagination justify-content-center">
          <li class="page-item" [class.disabled]="currentPage === 0">
            <button class="page-link" (click)="loadPage(currentPage - 1)">Anterior</button>
          </li>
          @for (page of getPageNumbers(); track page) {
            <li class="page-item" [class.active]="page === currentPage">
              <button class="page-link" (click)="loadPage(page)">{{ page + 1 }}</button>
            </li>
          }
          <li class="page-item" [class.disabled]="currentPage >= totalPages - 1">
            <button class="page-link" (click)="loadPage(currentPage + 1)">Siguiente</button>
          </li>
        </ul>
      </nav>
    </div>
  `
})
export class TournamentsListComponent implements OnInit {
  tournaments$ = new BehaviorSubject<Tournament[]>([]);
  currentPage = 0;
  pageSize = 12;
  totalPages = 0;
  totalElements = 0;

  filters: TournamentFilters = {
    status: '',
    isPaid: '',
    categoryId: '',
    gameTypeId: ''
  };

  constructor(private tournamentsService: TournamentsService) {}

  ngOnInit(): void {
    this.loadTournaments();
  }

  loadTournaments(): void {
    this.tournamentsService.getTournaments(this.currentPage, this.pageSize, this.filters)
      .subscribe({
        next: (response) => {
          this.tournaments$.next(response.content);
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
        },
        error: (error) => {
          console.error('Error loading tournaments:', error);
        }
      });
  }

  applyFilters(): void {
    this.currentPage = 0;
    this.loadTournaments();
  }

  loadPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadTournaments();
    }
  }

  publishTournament(id: string): void {
    this.tournamentsService.publishTournament(id).subscribe({
      next: () => {
        this.loadTournaments(); // Refresh list
      },
      error: (error) => {
        console.error('Error publishing tournament:', error);
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    const classes = {
      'DRAFT': 'bg-secondary',
      'PUBLISHED': 'bg-success',
      'FINISHED': 'bg-primary',
      'CANCELLED': 'bg-danger'
    };
    return classes[status as keyof typeof classes] || 'bg-secondary';
  }

  getPageNumbers(): number[] {
    const pages = [];
    const start = Math.max(0, this.currentPage - 2);
    const end = Math.min(this.totalPages, start + 5);
    
    for (let i = start; i < end; i++) {
      pages.push(i);
    }
    
    return pages;
  }
}
```

### 6.2 Authentication Feature

#### LoginComponent
```typescript
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  template: `
    <div class="container-fluid vh-100 d-flex align-items-center justify-content-center bg-light">
      <div class="card shadow" style="width: 400px;">
        <div class="card-body p-4">
          <div class="text-center mb-4">
            <h3 class="card-title">Iniciar Sesión</h3>
            <p class="text-muted">Plataforma de Torneos E-Sport</p>
          </div>

          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
            <div class="mb-3">
              <label for="email" class="form-label">Email</label>
              <input
                type="email"
                id="email"
                class="form-control"
                formControlName="email"
                [class.is-invalid]="loginForm.get('email')?.invalid && loginForm.get('email')?.touched"
                placeholder="usuario@ejemplo.com"
              >
              <div class="invalid-feedback">
                Email es requerido y debe ser válido
              </div>
            </div>

            <button
              type="submit"
              class="btn btn-primary w-100"
              [disabled]="loginForm.invalid || isLoading"
            >
              @if (isLoading) {
                <span class="spinner-border spinner-border-sm me-2"></span>
              }
              Iniciar Sesión
            </button>
          </form>

          @if (errorMessage) {
            <div class="alert alert-danger mt-3" role="alert">
              {{ errorMessage }}
            </div>
          }

          <div class="text-center mt-4">
            <small class="text-muted">
              ¿No tienes cuenta? 
              <a routerLink="/register" class="text-decoration-none">Regístrate aquí</a>
            </small>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    // Check if already logged in
    if (this.authService.getCurrentUser()) {
      this.router.navigate(['/tournaments']);
    }
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const email = this.loginForm.get('email')?.value;

      this.authService.login(email).subscribe({
        next: (response) => {
          this.isLoading = false;
          
          // Redirect to return URL or default to tournaments
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/tournaments';
          this.router.navigate([returnUrl]);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.message || 'Error al iniciar sesión';
        }
      });
    }
  }
}
```

## 7. Routing y Navegación

### 7.1 App Routes
```typescript
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
    path: '',
    redirectTo: '/tournaments',
    pathMatch: 'full'
  }
];
```

### 7.2 Lazy Loading
Todas las rutas utilizan **lazy loading** para optimizar el rendimiento:
- Carga inicial más rápida
- Splitting automático de código
- Mejor experiencia de usuario

## 8. Comunicación con Backend

### 8.1 Configuración de Entorno
```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8081/api'
};

// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.torneos.com/api'
};
```

### 8.2 Proxy Configuration
```json
{
  "/api/*": {
    "target": "http://localhost:8081",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

### 8.3 Error Handling
```typescript
// Manejo centralizado de errores
export class ErrorHandlingService {
  
  handleError(error: any): void {
    switch (error.status) {
      case 400:
        this.showValidationErrors(error.error.validationErrors);
        break;
      case 401:
        this.redirectToLogin();
        break;
      case 403:
        this.showAccessDeniedMessage();
        break;
      case 422:
        this.showBusinessRuleError(error.message);
        break;
      default:
        this.showGenericError();
    }
  }

  private showValidationErrors(errors: any): void {
    // Show validation errors in UI
  }

  private redirectToLogin(): void {
    // Clear tokens and redirect
  }
}
```

## 9. Estado y Gestión de Datos

### 9.1 Reactive Programming con RxJS
```typescript
@Injectable()
export class TournamentStateService {
  private tournamentsSubject = new BehaviorSubject<Tournament[]>([]);
  public tournaments$ = this.tournamentsSubject.asObservable();

  private selectedTournamentSubject = new BehaviorSubject<Tournament | null>(null);
  public selectedTournament$ = this.selectedTournamentSubject.asObservable();

  updateTournaments(tournaments: Tournament[]): void {
    this.tournamentsSubject.next(tournaments);
  }

  selectTournament(tournament: Tournament): void {
    this.selectedTournamentSubject.next(tournament);
  }

  addTournament(tournament: Tournament): void {
    const current = this.tournamentsSubject.value;
    this.tournamentsSubject.next([...current, tournament]);
  }

  updateTournament(updatedTournament: Tournament): void {
    const current = this.tournamentsSubject.value;
    const index = current.findIndex(t => t.id === updatedTournament.id);
    if (index !== -1) {
      current[index] = updatedTournament;
      this.tournamentsSubject.next([...current]);
    }
  }
}
```

### 9.2 Local Storage Service
```typescript
@Injectable({
  providedIn: 'root'
})
export class StorageService {
  
  setItem(key: string, value: any): void {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.error('Error saving to localStorage:', error);
    }
  }

  getItem<T>(key: string): T | null {
    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : null;
    } catch (error) {
      console.error('Error reading from localStorage:', error);
      return null;
    }
  }

  removeItem(key: string): void {
    localStorage.removeItem(key);
  }

  clear(): void {
    localStorage.clear();
  }
}
```

## 10. Estilos y UI

### 10.1 Bootstrap Integration
```scss
// styles.scss
@import 'bootstrap/scss/bootstrap';

// Custom variables
:root {
  --primary-color: #007bff;
  --secondary-color: #6c757d;
  --success-color: #28a745;
  --danger-color: #dc3545;
  --warning-color: #ffc107;
  --info-color: #17a2b8;
}

// Global styles
body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.card {
  border-radius: 0.5rem;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}

.btn {
  border-radius: 0.375rem;
}
```

### 10.2 Component Styles
```scss
// tournament-card.component.scss
.tournament-card {
  transition: transform 0.2s ease-in-out;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
  }
  
  .status-badge {
    &.draft { background-color: var(--secondary-color); }
    &.published { background-color: var(--success-color); }
    &.finished { background-color: var(--primary-color); }
    &.cancelled { background-color: var(--danger-color); }
  }
}
```

## 11. Testing Strategy

### 11.1 Unit Tests
```typescript
describe('TournamentsService', () => {
  let service: TournamentsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TournamentsService]
    });
    
    service = TestBed.inject(TournamentsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should fetch tournaments', () => {
    const mockTournaments = [
      { id: '1', name: 'Test Tournament', status: 'PUBLISHED' }
    ];

    service.getTournaments().subscribe(tournaments => {
      expect(tournaments.content).toEqual(jasmine.any(Array));
      expect(tournaments.content[0]).toBeInstanceOf(Tournament);
    });

    const req = httpMock.expectOne('/api/tournaments');
    expect(req.request.method).toBe('GET');
    req.flush({ content: mockTournaments, totalElements: 1 });
  });
});
```

### 11.2 Component Tests
```typescript
describe('TournamentsListComponent', () => {
  let component: TournamentsListComponent;
  let fixture: ComponentFixture<TournamentsListComponent>;
  let tournamentsService: jasmine.SpyObj<TournamentsService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('TournamentsService', ['getTournaments']);

    TestBed.configureTestingModule({
      imports: [TournamentsListComponent],
      providers: [
        { provide: TournamentsService, useValue: spy }
      ]
    });

    fixture = TestBed.createComponent(TournamentsListComponent);
    component = fixture.componentInstance;
    tournamentsService = TestBed.inject(TournamentsService) as jasmine.SpyObj<TournamentsService>;
  });

  it('should load tournaments on init', () => {
    const mockResponse = {
      content: [new Tournament({ id: '1', name: 'Test' })],
      totalElements: 1,
      totalPages: 1
    };

    tournamentsService.getTournaments.and.returnValue(of(mockResponse));

    component.ngOnInit();

    expect(tournamentsService.getTournaments).toHaveBeenCalled();
  });
});
```

## 12. Build y Deployment

### 12.1 Development Build
```bash
# Desarrollo
ng serve --proxy-config proxy.conf.json

# Con configuración específica
ng serve --configuration development
```

### 12.2 Production Build
```bash
# Build de producción
ng build --configuration production

# Optimizaciones incluidas:
# - Tree shaking
# - Minification
# - Bundle optimization
# - AOT compilation
```

### 12.3 Docker Configuration
```dockerfile
# Frontend Dockerfile
FROM node:18-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build --prod

FROM nginx:alpine
COPY --from=builder /app/dist/frontend-torneos /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

**Conclusión**: El frontend implementa una arquitectura moderna y escalable con Angular 17, proporcionando una experiencia de usuario fluida, comunicación eficiente con el backend, manejo robusto de errores y autenticación, y está preparado para evolucionar con nuevas funcionalidades y mejoras de performance.