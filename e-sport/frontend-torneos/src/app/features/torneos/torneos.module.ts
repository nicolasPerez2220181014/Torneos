import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { TorneosListComponent } from './pages/torneos-list.component';

const routes: Routes = [
  { path: '', component: TorneosListComponent }
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    TorneosListComponent
  ]
})
export class TorneosModule { }