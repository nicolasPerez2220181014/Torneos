import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TorneosService, Tournament } from '../services/torneos.service';
import { PaginatedResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-torneos-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <h2>Lista de Torneos</h2>
      
      <div *ngIf="loading" class="loading">
        Cargando torneos...
      </div>
      
      <div *ngIf="error" class="error">
        Error: {{ error }}
      </div>
      
      <div *ngIf="tournaments && !loading" class="success">
        <p>Conexión con backend exitosa ✅</p>
        <p>Total de torneos: {{ tournaments.pageable.totalElements }}</p>
        
        <div class="tournaments-grid">
          <div *ngFor="let tournament of tournaments.content" class="tournament-card">
            <h3>{{ tournament.name }}</h3>
            <p>{{ tournament.description }}</p>
            <span class="status" [class]="tournament.status.toLowerCase()">
              {{ tournament.status }}
            </span>
            <p><strong>Tipo:</strong> {{ tournament.isPaid ? 'Pagado' : 'Gratuito' }}</p>
            <p><strong>Inicio:</strong> {{ tournament.startDateTime | date:'short' }}</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container {
      padding: 20px;
      max-width: 1200px;
      margin: 0 auto;
    }
    .loading {
      color: #007bff;
      font-weight: bold;
    }
    .error {
      color: #dc3545;
      background: #f8d7da;
      padding: 10px;
      border-radius: 4px;
    }
    .success {
      color: #155724;
    }
    .tournaments-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;
      margin-top: 20px;
    }
    .tournament-card {
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 16px;
      background: white;
    }
    .status {
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: bold;
    }
    .status.published { background: #d4edda; color: #155724; }
    .status.draft { background: #fff3cd; color: #856404; }
    .status.finished { background: #d1ecf1; color: #0c5460; }
    .status.cancelled { background: #f8d7da; color: #721c24; }
  `]
})
export class TorneosListComponent implements OnInit {
  tournaments: PaginatedResponse<Tournament> | null = null;
  loading = false;
  error: string | null = null;

  constructor(private torneosService: TorneosService) {}

  ngOnInit(): void {
    this.loadTournaments();
  }

  loadTournaments(): void {
    this.loading = true;
    this.error = null;
    
    this.torneosService.getTournaments().subscribe({
      next: (response) => {
        this.tournaments = response;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }
}