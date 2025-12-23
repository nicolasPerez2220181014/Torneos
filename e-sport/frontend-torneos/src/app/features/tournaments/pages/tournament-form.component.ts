import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TournamentsService } from '../services/tournaments.service';
import { CategoriesService } from '../../categories/services/categories.service';
import { GameTypesService } from '../../game-types/services/game-types.service';
import { Tournament, TournamentRequest } from '../../../core/models/tournament.models';
import { Category, GameType } from '../../../core/models/masters.models';

@Component({
  selector: 'app-tournament-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>{{isEdit ? 'Editar' : 'Crear'}} Torneo</h2>
        <button class="btn btn-outline" (click)="goBack()">
          Volver
        </button>
      </div>

      <div *ngIf="loading" class="loading">
        Cargando...
      </div>

      <div *ngIf="error" class="error">
        {{error}}
      </div>

      <form *ngIf="!loading" (ngSubmit)="onSubmit()" #tournamentForm="ngForm" class="tournament-form">
        <div class="form-row">
          <div class="form-group">
            <label for="name">Nombre del Torneo *</label>
            <input 
              type="text" 
              id="name"
              name="name"
              [(ngModel)]="tournament.name"
              required
              maxlength="100"
              class="form-control"
              placeholder="Ingrese el nombre del torneo">
          </div>

          <div class="form-group">
            <label for="maxParticipants">Máximo Participantes *</label>
            <input 
              type="number" 
              id="maxParticipants"
              name="maxParticipants"
              [(ngModel)]="tournament.maxFreeCapacity"
              required
              min="1"
              max="10000"
              class="form-control"
              placeholder="Ej: 100">
          </div>
        </div>

        <div class="form-group">
          <label for="description">Descripción *</label>
          <textarea 
            id="description"
            name="description"
            [(ngModel)]="tournament.description"
            required
            maxlength="500"
            rows="4"
            class="form-control"
            placeholder="Describe el torneo, reglas, premios, etc."></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="categoryId">Categoría *</label>
            <select 
              id="categoryId"
              name="categoryId"
              [(ngModel)]="tournament.categoryId"
              required
              class="form-control">
              <option value="">Seleccione una categoría</option>
              <option *ngFor="let category of categories" [value]="category.id">
                {{category.name}}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="gameTypeId">Tipo de Juego *</label>
            <select 
              id="gameTypeId"
              name="gameTypeId"
              [(ngModel)]="tournament.gameTypeId"
              required
              class="form-control">
              <option value="">Seleccione un tipo de juego</option>
              <option *ngFor="let gameType of gameTypes" [value]="gameType.id">
                {{gameType.name}}
              </option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="startDate">Fecha de Inicio *</label>
            <input 
              type="datetime-local" 
              id="startDate"
              name="startDate"
              [(ngModel)]="formData.startDate"
              required
              class="form-control">
          </div>

          <div class="form-group">
            <label for="endDate">Fecha de Fin *</label>
            <input 
              type="datetime-local" 
              id="endDate"
              name="endDate"
              [(ngModel)]="formData.endDate"
              required
              class="form-control">
          </div>
        </div>

        <div class="form-group">
          <label>
            <input 
              type="checkbox" 
              name="isPaid"
              [(ngModel)]="tournament.isPaid"
              class="form-checkbox">
            Torneo de pago
          </label>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-outline" (click)="goBack()">
            Cancelar
          </button>
          <button 
            type="submit" 
            class="btn btn-primary"
            [disabled]="!tournamentForm.valid || saving">
            {{saving ? 'Guardando...' : (isEdit ? 'Actualizar' : 'Crear')}}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .container { max-width: 800px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .tournament-form { background: white; padding: 30px; border-radius: 8px; border: 1px solid #ddd; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
    .form-group { margin-bottom: 20px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; color: #333; }
    .form-control { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
    .form-control:focus { outline: none; border-color: #1976d2; box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2); }
    .form-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; }
    .loading, .error { text-align: center; padding: 40px; }
    .error { color: #d32f2f; }
    .btn { padding: 10px 20px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; font-size: 14px; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-outline { background: white; color: #666; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    .form-checkbox { margin-right: 8px; }
    @media (max-width: 768px) {
      .form-row { grid-template-columns: 1fr; }
      .container { padding: 10px; }
      .tournament-form { padding: 20px; }
    }
  `]
})
export class TournamentFormComponent implements OnInit {
  tournament: TournamentRequest = {
    name: '',
    description: '',
    startDateTime: '',
    endDateTime: '',
    maxFreeCapacity: 100,
    isPaid: false,
    categoryId: '',
    gameTypeId: ''
  };

  formData = {
    startDate: '',
    endDate: ''
  };

  categories: Category[] = [];
  gameTypes: GameType[] = [];
  loading = false;
  saving = false;
  error: string | null = null;
  isEdit = false;
  tournamentId: number | null = null;

  constructor(
    private tournamentsService: TournamentsService,
    private categoriesService: CategoriesService,
    private gameTypesService: GameTypesService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.tournamentId = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.tournamentId && !isNaN(this.tournamentId);

    this.loadMasterData();
    
    if (this.isEdit) {
      this.loadTournament();
    }
  }

  loadMasterData() {
    this.categoriesService.getCategories().subscribe({
      next: (categories) => this.categories = categories,
      error: (error) => console.error('Error loading categories:', error)
    });

    this.gameTypesService.getGameTypes().subscribe({
      next: (gameTypes) => this.gameTypes = gameTypes,
      error: (error) => console.error('Error loading game types:', error)
    });
  }

  loadTournament() {
    if (!this.tournamentId) return;

    this.loading = true;
    this.tournamentsService.getTournament(this.tournamentId).subscribe({
      next: (tournament) => {
        this.tournament = {
          name: tournament.name,
          description: tournament.description,
          startDateTime: tournament.startDateTime,
          endDateTime: tournament.endDateTime,
          maxFreeCapacity: tournament.maxFreeCapacity,
          isPaid: tournament.isPaid,
          categoryId: tournament.categoryId,
          gameTypeId: tournament.gameTypeId
        };
        
        this.formData = {
          startDate: this.formatDateForInput(tournament.startDateTime),
          endDate: this.formatDateForInput(tournament.endDateTime)
        };
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar el torneo';
        this.loading = false;
        console.error('Error loading tournament:', error);
      }
    });
  }

  onSubmit() {
    if (this.saving) return;

    this.saving = true;
    this.error = null;

    // Map form data to backend format
    const tournamentData: TournamentRequest = {
      name: this.tournament.name,
      description: this.tournament.description,
      startDateTime: this.formData.startDate,
      endDateTime: this.formData.endDate,
      maxFreeCapacity: this.tournament.maxFreeCapacity,
      isPaid: this.tournament.isPaid,
      categoryId: this.tournament.categoryId,
      gameTypeId: this.tournament.gameTypeId
    };
    
    console.log('Sending tournament data:', tournamentData);

    const operation = this.isEdit && this.tournamentId
      ? this.tournamentsService.updateTournament(this.tournamentId, tournamentData)
      : this.tournamentsService.createTournament(tournamentData);

    operation.subscribe({
      next: (response) => {
        console.log('Tournament created successfully:', response);
        this.saving = false;
        this.router.navigate(['/tournaments']);
      },
      error: (error) => {
        console.error('Full error object:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        console.error('Error body:', error.error);
        
        this.error = error.error?.message || error.message || 'Error al crear el torneo';
        this.saving = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/tournaments']);
  }

  private formatDateForInput(dateString: string): string {
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16);
  }
}