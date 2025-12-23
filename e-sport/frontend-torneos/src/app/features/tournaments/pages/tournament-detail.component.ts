import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TournamentsService } from '../services/tournaments.service';
import { Tournament, SubAdmin, TournamentStatus } from '../../../core/models/tournament.models';
import { UserSearchResult } from '../../../core/models/user.models';
import { UserSelectorComponent } from '../../../shared/components/user-selector.component';
import { StreamSettingsComponent } from '../../stream-control/pages/stream-settings.component';

@Component({
  selector: 'app-tournament-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, UserSelectorComponent, StreamSettingsComponent],
  template: `
    <div class="container">
      <div class="header">
        <h2>Detalle del Torneo</h2>
        <div class="header-actions">
          <button class="btn btn-outline" (click)="editTournament()" *ngIf="tournament">
            Editar
          </button>
          <button class="btn btn-outline" (click)="goBack()">
            Volver
          </button>
        </div>
      </div>

      <div *ngIf="loading" class="loading">
        Cargando...
      </div>

      <div *ngIf="error" class="error">
        {{error}}
      </div>

      <div *ngIf="tournament && !loading" class="detail-container">
        <!-- Información Principal -->
        <div class="card">
          <div class="card-header">
            <h3>{{tournament.name}}</h3>
            <span class="status" [class]="'status-' + tournament.status.toLowerCase()">
              {{getStatusLabel(tournament.status)}}
            </span>
          </div>

          <div class="card-body">
            <div class="info-grid">
              <div class="info-item">
                <label>Descripción:</label>
                <p>{{tournament.description}}</p>
              </div>

              <div class="info-item">
                <label>Categoría:</label>
                <p>{{tournament.category?.name || 'N/A'}}</p>
              </div>

              <div class="info-item">
                <label>Tipo de Juego:</label>
                <p>{{tournament.gameType?.name || 'N/A'}}</p>
              </div>

              <div class="info-item">
                <label>Máximo Participantes:</label>
                <p>{{tournament.maxParticipants}}</p>
              </div>

              <div class="info-item">
                <label>Fecha de Inicio:</label>
                <p>{{formatDateTime(tournament.startDate)}}</p>
              </div>

              <div class="info-item">
                <label>Fecha de Fin:</label>
                <p>{{formatDateTime(tournament.endDate)}}</p>
              </div>

              <div class="info-item">
                <label>Inicio de Inscripciones:</label>
                <p>{{formatDateTime(tournament.registrationStartDate)}}</p>
              </div>

              <div class="info-item">
                <label>Fin de Inscripciones:</label>
                <p>{{formatDateTime(tournament.registrationEndDate)}}</p>
              </div>

              <div class="info-item">
                <label>Creado:</label>
                <p>{{formatDateTime(tournament.createdAt)}}</p>
              </div>

              <div class="info-item">
                <label>Actualizado:</label>
                <p>{{formatDateTime(tournament.updatedAt)}}</p>
              </div>
            </div>

            <div class="actions" *ngIf="tournament.status === 'DRAFT'">
              <button class="btn btn-success" (click)="publishTournament()">
                Publicar Torneo
              </button>
            </div>
          </div>
        </div>

        <!-- Subadministradores -->
        <div class="card">
          <div class="card-header">
            <h3>Subadministradores</h3>
          </div>

          <div class="card-body">
            <div *ngIf="loadingSubAdmins" class="loading-small">
              Cargando subadministradores...
            </div>

            <div *ngIf="!loadingSubAdmins && subAdmins.length === 0" class="empty-state">
              No hay subadministradores asignados
            </div>

            <div *ngIf="!loadingSubAdmins && subAdmins.length > 0" class="subadmins-list">
              <div *ngFor="let subAdmin of subAdmins" class="subadmin-item">
                <div class="subadmin-info">
                  <strong>{{subAdmin.firstName}} {{subAdmin.lastName}}</strong>
                  <span class="email">{{subAdmin.email}}</span>
                  <span class="date">Asignado: {{formatDate(subAdmin.assignedAt)}}</span>
                </div>
                <button class="btn btn-sm btn-danger" (click)="removeSubAdmin(subAdmin)">
                  Remover
                </button>
              </div>
            </div>

            <div class="add-subadmin">
              <app-user-selector 
                buttonText="Asignar"
                (userSelected)="onUserSelected($event)">
              </app-user-selector>
            </div>
          </div>
        </div>

        <!-- Configuración de Tickets -->
        <div class="card">
          <div class="card-header">
            <h3>Configuración de Tickets</h3>
          </div>
          <div class="card-body">
            <p>Configuración de etapas de venta disponible en desarrollo.</p>
          </div>
        </div>

        <!-- Gestión de Stream -->
        <div class="card">
          <div class="card-header">
            <h3>Stream del Torneo</h3>
          </div>
          <div class="card-body">
            <app-stream-settings [tournamentId]="tournament.id"></app-stream-settings>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 1000px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .header-actions { display: flex; gap: 10px; }
    .detail-container { display: flex; flex-direction: column; gap: 20px; }
    .card { background: white; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; }
    .card-header { padding: 20px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }
    .card-header h3 { margin: 0; }
    .card-body { padding: 20px; }
    .status { padding: 6px 12px; border-radius: 4px; font-size: 12px; font-weight: bold; }
    .status-draft { background: #f0f0f0; color: #666; }
    .status-published { background: #e3f2fd; color: #1976d2; }
    .status-registration_open { background: #e8f5e8; color: #2e7d32; }
    .status-in_progress { background: #fff3e0; color: #f57c00; }
    .status-completed { background: #f3e5f5; color: #7b1fa2; }
    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
    .info-item label { display: block; font-weight: bold; color: #666; margin-bottom: 5px; font-size: 14px; }
    .info-item p { margin: 0; color: #333; }
    .actions { margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee; }
    .subadmins-list { margin-bottom: 20px; }
    .subadmin-item { display: flex; justify-content: space-between; align-items: center; padding: 15px; border: 1px solid #eee; border-radius: 4px; margin-bottom: 10px; }
    .subadmin-info { display: flex; flex-direction: column; gap: 5px; }
    .subadmin-info strong { color: #333; }
    .subadmin-info .email { color: #666; font-size: 14px; }
    .subadmin-info .date { color: #999; font-size: 12px; }
    .add-subadmin { display: flex; gap: 10px; padding-top: 20px; border-top: 1px solid #eee; }
    .add-subadmin .form-control { flex: 1; }
    .empty-state { text-align: center; padding: 40px; color: #999; }
    .loading, .loading-small { text-align: center; padding: 40px; color: #666; }
    .loading-small { padding: 20px; }
    .error { text-align: center; padding: 40px; color: #d32f2f; }
    .btn { padding: 10px 20px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-success { background: #2e7d32; color: white; border-color: #2e7d32; }
    .btn-danger { background: #d32f2f; color: white; border-color: #d32f2f; }
    .btn-outline { background: white; color: #666; }
    .btn-sm { padding: 6px 12px; font-size: 12px; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .form-control { padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
    @media (max-width: 768px) {
      .info-grid { grid-template-columns: 1fr; }
      .header { flex-direction: column; align-items: flex-start; gap: 10px; }
    }
  `]
})
export class TournamentDetailComponent implements OnInit {
  tournament: Tournament | null = null;
  subAdmins: SubAdmin[] = [];
  loading = false;
  loadingSubAdmins = false;
  assigningSubAdmin = false;
  error: string | null = null;
  newSubAdminId: number | null = null;

  statusLabels = {
    [TournamentStatus.DRAFT]: 'Borrador',
    [TournamentStatus.PUBLISHED]: 'Publicado',
    [TournamentStatus.REGISTRATION_OPEN]: 'Inscripciones Abiertas',
    [TournamentStatus.REGISTRATION_CLOSED]: 'Inscripciones Cerradas',
    [TournamentStatus.IN_PROGRESS]: 'En Progreso',
    [TournamentStatus.COMPLETED]: 'Completado',
    [TournamentStatus.CANCELLED]: 'Cancelado'
  };

  constructor(
    private tournamentsService: TournamentsService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadTournament(id);
      this.loadSubAdmins(id);
    }
  }

  loadTournament(id: number | string) {
    this.loading = true;
    this.tournamentsService.getTournament(id).subscribe({
      next: (tournament) => {
        this.tournament = tournament;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar el torneo';
        this.loading = false;
        console.error('Error loading tournament:', error);
      }
    });
  }

  loadSubAdmins(tournamentId: number | string) {
    this.loadingSubAdmins = true;
    this.tournamentsService.getSubAdmins(tournamentId).subscribe({
      next: (subAdmins) => {
        this.subAdmins = subAdmins;
        this.loadingSubAdmins = false;
      },
      error: (error) => {
        this.loadingSubAdmins = false;
        console.error('Error loading subadmins:', error);
      }
    });
  }

  onUserSelected(user: UserSearchResult) {
    this.assignSubAdminByUser(user.id);
  }

  assignSubAdminByUser(userId: number) {
    if (!this.tournament) return;

    this.assigningSubAdmin = true;
    this.tournamentsService.assignSubAdmin(this.tournament.id, { subAdminId: userId }).subscribe({
      next: () => {
        this.assigningSubAdmin = false;
        this.loadSubAdmins(this.tournament!.id);
      },
      error: (error) => {
        this.assigningSubAdmin = false;
        console.error('Error assigning subadmin:', error);
        alert('Error al asignar subadministrador');
      }
    });
  }

  assignSubAdmin() {
    if (!this.tournament || !this.newSubAdminId) return;

    this.assigningSubAdmin = true;
    this.tournamentsService.assignSubAdmin(this.tournament.id, { subAdminId: this.newSubAdminId }).subscribe({
      next: () => {
        this.newSubAdminId = null;
        this.assigningSubAdmin = false;
        this.loadSubAdmins(this.tournament!.id);
      },
      error: (error) => {
        this.assigningSubAdmin = false;
        console.error('Error assigning subadmin:', error);
        alert('Error al asignar subadministrador');
      }
    });
  }

  removeSubAdmin(subAdmin: SubAdmin) {
    if (!this.tournament) return;

    if (confirm(`¿Está seguro de remover a ${subAdmin.firstName} ${subAdmin.lastName}?`)) {
      this.tournamentsService.removeSubAdmin(this.tournament.id, subAdmin.id).subscribe({
        next: () => {
          this.loadSubAdmins(this.tournament!.id);
        },
        error: (error) => {
          console.error('Error removing subadmin:', error);
          alert('Error al remover subadministrador');
        }
      });
    }
  }

  publishTournament() {
    if (!this.tournament) return;

    if (confirm(`¿Está seguro de publicar el torneo "${this.tournament.name}"?`)) {
      this.tournamentsService.publishTournament(this.tournament.id).subscribe({
        next: () => {
          this.loadTournament(this.tournament!.id);
        },
        error: (error) => {
          console.error('Error publishing tournament:', error);
          alert('Error al publicar el torneo');
        }
      });
    }
  }

  editTournament() {
    if (this.tournament) {
      this.router.navigate(['/tournaments', this.tournament.id, 'edit']);
    }
  }

  goBack() {
    this.router.navigate(['/tournaments']);
  }

  getStatusLabel(status: TournamentStatus): string {
    return this.statusLabels[status] || status;
  }

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleString('es-ES');
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-ES');
  }
}