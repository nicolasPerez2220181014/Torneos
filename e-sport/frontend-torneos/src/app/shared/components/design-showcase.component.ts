import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-design-showcase',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container py-3xl">
      <!-- Header Section -->
      <div class="text-center mb-4xl">
        <h1 class="hero-title mb-lg">Sistema de Diseño E-Sports</h1>
        <p class="hero-subtitle">Componentes modernos y profesionales para tu aplicación de torneos</p>
      </div>

      <!-- Cards Section -->
      <section class="mb-4xl">
        <h2 class="section-title mb-xl">Cards y Componentes</h2>
        <div class="grid gap-xl">
          <div class="card">
            <div class="card-header">
              <h3>Torneo de Valorant</h3>
              <p>Competencia profesional con premios en efectivo</p>
            </div>
            <div class="card-body">
              <div class="flex items-center gap-md mb-lg">
                <span class="badge badge-success">En vivo</span>
                <span class="badge badge-info">32 equipos</span>
              </div>
              <p class="text-secondary">
                Únete al torneo más emocionante del año. Equipos profesionales compitiendo por el gran premio.
              </p>
            </div>
            <div class="card-footer">
              <button class="btn-ghost">Ver detalles</button>
              <button class="btn-primary">Inscribirse</button>
            </div>
          </div>

          <div class="card">
            <div class="card-header">
              <h3>League of Legends Championship</h3>
              <p>Torneo amateur con clasificación regional</p>
            </div>
            <div class="card-body">
              <div class="flex items-center gap-md mb-lg">
                <span class="badge badge-warning">Próximamente</span>
                <span class="badge badge-info">16 equipos</span>
              </div>
              <p class="text-secondary">
                Demuestra tus habilidades en el Rift. Torneo clasificatorio para la liga regional.
              </p>
            </div>
            <div class="card-footer">
              <button class="btn-ghost">Ver detalles</button>
              <button class="btn-accent">Reservar lugar</button>
            </div>
          </div>
        </div>
      </section>

      <!-- Buttons Section -->
      <section class="mb-4xl">
        <h2 class="section-title mb-xl">Botones</h2>
        <div class="flex flex-wrap gap-md">
          <button class="btn-primary">Primario</button>
          <button class="btn-secondary">Secundario</button>
          <button class="btn-accent">Acento</button>
          <button class="btn-ghost">Fantasma</button>
          <button class="btn-primary btn-sm">Pequeño</button>
          <button class="btn-primary btn-lg">Grande</button>
        </div>
      </section>

      <!-- Form Section -->
      <section class="mb-4xl">
        <h2 class="section-title mb-xl">Formularios</h2>
        <div class="card" style="max-width: 500px;">
          <div class="card-header">
            <h3>Registro de Equipo</h3>
            <p>Completa la información de tu equipo</p>
          </div>
          <div class="card-body">
            <div class="form-group">
              <label>Nombre del equipo</label>
              <input type="text" class="form-control" placeholder="Ingresa el nombre de tu equipo">
            </div>
            <div class="form-group">
              <label>Juego</label>
              <select class="form-control">
                <option>Selecciona un juego</option>
                <option>Valorant</option>
                <option>League of Legends</option>
                <option>CS:GO</option>
              </select>
            </div>
            <div class="form-group">
              <label>Descripción</label>
              <textarea class="form-control" rows="3" placeholder="Describe tu equipo..."></textarea>
              <div class="form-help">Máximo 200 caracteres</div>
            </div>
          </div>
          <div class="card-footer">
            <button class="btn-ghost">Cancelar</button>
            <button class="btn-primary">Registrar equipo</button>
          </div>
        </div>
      </section>

      <!-- Table Section -->
      <section class="mb-4xl">
        <h2 class="section-title mb-xl">Tabla de Clasificación</h2>
        <div class="table-container">
          <table class="table">
            <thead>
              <tr>
                <th>Posición</th>
                <th>Equipo</th>
                <th>Puntos</th>
                <th>Partidas</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>1</td>
                <td>Team Alpha</td>
                <td>2,450</td>
                <td>12/15</td>
                <td><span class="badge badge-success">Activo</span></td>
              </tr>
              <tr>
                <td>2</td>
                <td>Beta Squad</td>
                <td>2,200</td>
                <td>11/15</td>
                <td><span class="badge badge-success">Activo</span></td>
              </tr>
              <tr>
                <td>3</td>
                <td>Gamma Force</td>
                <td>1,980</td>
                <td>10/15</td>
                <td><span class="badge badge-warning">Pendiente</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Loading States -->
      <section class="mb-4xl">
        <h2 class="section-title mb-xl">Estados de Carga</h2>
        <div class="flex items-center gap-lg">
          <button class="btn-primary" disabled>
            <span class="loading"></span>
            Cargando...
          </button>
          <div class="flex items-center gap-md">
            <span class="loading"></span>
            <span>Procesando datos...</span>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .hero-title {
      font-size: var(--font-size-4xl);
      font-weight: var(--font-weight-bold);
      background: var(--gradient-primary);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    
    .hero-subtitle {
      font-size: var(--font-size-lg);
      color: var(--color-text-secondary);
      max-width: 600px;
      margin: 0 auto;
    }
    
    .section-title {
      font-size: var(--font-size-2xl);
      font-weight: var(--font-weight-semibold);
      color: var(--color-text-primary);
    }
    
    .text-secondary {
      color: var(--color-text-secondary);
    }
    
    .grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    }
    
    .table-container {
      overflow-x: auto;
      border-radius: var(--border-radius-lg);
    }
    
    @media (max-width: 768px) {
      .grid {
        grid-template-columns: 1fr;
      }
      
      .flex {
        flex-direction: column;
        align-items: stretch !important;
      }
      
      .flex button {
        width: 100%;
      }
    }
  `]
})
export class DesignShowcaseComponent {}