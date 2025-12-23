import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MasterCrudComponent, MasterItem } from '../../../shared/components/master-crud.component';
import { GameTypesService } from '../services/game-types.service';
import { GameType } from '../../../core/models/masters.models';

@Component({
  selector: 'app-game-types-list',
  standalone: true,
  imports: [CommonModule, MasterCrudComponent],
  template: `
    <app-master-crud
      title="Tipos de Juego"
      entityName="Tipo de Juego"
      [items]="gameTypes"
      [loading]="loading"
      [error]="error"
      (create)="onCreateGameType($event)"
      (update)="onUpdateGameType($event)"
      (refresh)="loadGameTypes()">
    </app-master-crud>
  `
})
export class GameTypesListComponent implements OnInit {
  @ViewChild(MasterCrudComponent) masterCrud!: MasterCrudComponent;

  gameTypes: MasterItem[] | null = null;
  loading = false;
  error: string | null = null;

  constructor(private gameTypesService: GameTypesService) {}

  ngOnInit(): void {
    this.loadGameTypes();
  }

  loadGameTypes(): void {
    this.loading = true;
    this.error = null;

    this.gameTypesService.getGameTypes().subscribe({
      next: (gameTypes) => {
        this.gameTypes = gameTypes.map(gt => ({
          id: gt.id,
          name: gt.name,
          active: gt.active
        }));
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  onCreateGameType(name: string): void {
    this.loading = true;
    this.error = null;

    this.gameTypesService.createGameType({ name }).subscribe({
      next: () => {
        this.loading = false;
        this.masterCrud.resetForms();
        this.loadGameTypes();
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  onUpdateGameType(data: {id: string, name: string}): void {
    this.loading = true;
    this.error = null;

    this.gameTypesService.updateGameType(data.id, { name: data.name }).subscribe({
      next: () => {
        this.loading = false;
        this.masterCrud.resetForms();
        this.loadGameTypes();
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }
}