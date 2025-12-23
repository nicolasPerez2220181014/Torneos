import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { TournamentTestService } from './core/services/tournament-test.service';
import { TournamentRequest } from './core/models/tournament.models';

@Component({
  selector: 'app-test-tournament',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 20px;">
      <h2>Test Crear Torneo (Sin Auth)</h2>
      <button (click)="createTestTournament()" [disabled]="loading">
        {{loading ? 'Creando...' : 'Crear Torneo de Prueba'}}
      </button>
      
      <div *ngIf="result" style="margin-top: 20px; padding: 10px; border: 1px solid #4caf50; background: #e8f5e9;">
        <h3>✅ Éxito:</h3>
        <pre>{{result | json}}</pre>
      </div>
      
      <div *ngIf="error" style="margin-top: 20px; padding: 10px; background: #ffebee; color: #c62828;">
        <h3>❌ Error:</h3>
        <p>{{error}}</p>
      </div>
      
      <div style="margin-top: 20px; padding: 10px; background: #f5f5f5;">
        <h3>Datos que se enviarán:</h3>
        <pre>{{testData | json}}</pre>
      </div>
    </div>
  `
})
export class TestTournamentComponent {
  loading = false;
  result: any = null;
  error: string | null = null;
  testData: TournamentRequest;

  constructor(private testService: TournamentTestService, private http: HttpClient) {
    const now = new Date();
    const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
    const dayAfter = new Date(now.getTime() + 48 * 60 * 60 * 1000);
    
    this.testData = {
      name: 'Torneo de Prueba ' + now.getTime(),
      description: 'Este es un torneo de prueba creado desde el frontend',
      startDateTime: tomorrow.toISOString().slice(0, 16),
      endDateTime: dayAfter.toISOString().slice(0, 16),
      maxFreeCapacity: 50,
      isPaid: false,
      categoryId: '1',
      gameTypeId: '1'
    };
  }

  createTestTournament() {
    this.loading = true;
    this.result = null;
    this.error = null;

    console.log('Enviando datos:', this.testData);

    // Primero probar el endpoint de debug
    this.http.post('http://localhost:8081/api/debug/tournament', this.testData).subscribe({
      next: (debugResponse) => {
        console.log('Debug response:', debugResponse);
        
        // Ahora probar el endpoint real
        this.testService.createTournamentWithMockAuth(this.testData).subscribe({
          next: (response) => {
            this.result = response;
            this.loading = false;
            console.log('Torneo creado exitosamente:', response);
          },
          error: (error) => {
            console.error('Error completo:', error);
            this.error = `Status: ${error.status} - ${error.error?.message || error.message || 'Error desconocido'}`;
            this.loading = false;
          }
        });
      },
      error: (debugError) => {
        console.error('Error en debug:', debugError);
        this.error = 'Error en debug endpoint';
        this.loading = false;
      }
    });
  }
}