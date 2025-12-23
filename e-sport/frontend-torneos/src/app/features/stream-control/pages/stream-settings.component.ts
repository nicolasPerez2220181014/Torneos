import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StreamSettings, StreamQuality } from '../../../core/models/stream-control.models';

@Component({
  selector: 'app-stream-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="stream-settings">
      <h4>Configuración Avanzada</h4>

      <form (ngSubmit)="saveSettings()" #settingsForm="ngForm">
        <!-- Quality Settings -->
        <div class="settings-section">
          <h5>Calidad del Stream</h5>
          <div class="form-group">
            <label>Calidad Máxima:</label>
            <select [(ngModel)]="settings.quality" name="quality" class="form-control">
              <option value="LOW">Baja (480p) - Menor ancho de banda</option>
              <option value="MEDIUM">Media (720p) - Calidad estándar</option>
              <option value="HIGH">Alta (1080p) - Alta calidad</option>
              <option value="ULTRA">Ultra (4K) - Máxima calidad</option>
            </select>
          </div>
        </div>

        <!-- Viewer Limits -->
        <div class="settings-section">
          <h5>Límites de Audiencia</h5>
          <div class="form-group">
            <label>Máximo de Espectadores:</label>
            <input 
              type="number" 
              [(ngModel)]="settings.maxViewers"
              name="maxViewers"
              min="1"
              max="100000"
              class="form-control">
            <small>0 = Sin límite</small>
          </div>
        </div>

        <!-- Chat Settings -->
        <div class="settings-section">
          <h5>Configuración de Chat</h5>
          <div class="checkbox-group">
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                [(ngModel)]="settings.chatEnabled"
                name="chatEnabled">
              <span class="checkmark"></span>
              Habilitar chat en vivo
            </label>
            
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                [(ngModel)]="settings.moderationEnabled"
                name="moderationEnabled"
                [disabled]="!settings.chatEnabled">
              <span class="checkmark"></span>
              Activar moderación automática
            </label>
          </div>
        </div>

        <!-- Recording Settings -->
        <div class="settings-section">
          <h5>Grabación</h5>
          <div class="checkbox-group">
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                [(ngModel)]="settings.recordingEnabled"
                name="recordingEnabled">
              <span class="checkmark"></span>
              Grabar stream automáticamente
            </label>
            
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                [(ngModel)]="settings.autoStart"
                name="autoStart">
              <span class="checkmark"></span>
              Iniciar stream automáticamente
            </label>
          </div>
        </div>

        <!-- Stream Key -->
        <div class="settings-section">
          <h5>Clave de Stream</h5>
          <div class="form-group">
            <label>Stream Key:</label>
            <div class="key-input">
              <input 
                type="text" 
                [(ngModel)]="settings.streamKey"
                name="streamKey"
                readonly
                class="form-control">
              <button type="button" class="btn btn-outline" (click)="generateKey()">
                🔄 Regenerar
              </button>
            </div>
            <small>Usa esta clave en tu software de streaming (OBS, XSplit, etc.)</small>
          </div>
        </div>

        <!-- Actions -->
        <div class="settings-actions">
          <button type="button" class="btn btn-outline" (click)="resetSettings()">
            Restablecer
          </button>
          <button 
            type="submit" 
            class="btn btn-primary"
            [disabled]="saving">
            {{saving ? 'Guardando...' : 'Guardar Configuración'}}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .stream-settings { margin-top: 20px; }
    .stream-settings h4 { margin: 0 0 20px 0; }
    .stream-settings h5 { margin: 0 0 15px 0; font-size: 16px; color: #333; }
    .settings-section { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #ddd; }
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; color: #666; }
    .form-control { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
    .form-control:disabled { background: #f5f5f5; color: #999; }
    .form-group small { color: #666; font-size: 12px; }
    .checkbox-group { display: flex; flex-direction: column; gap: 15px; }
    .checkbox-label { display: flex; align-items: center; gap: 10px; cursor: pointer; }
    .checkbox-label input[type="checkbox"] { margin: 0; }
    .checkbox-label input[type="checkbox"]:disabled + .checkmark { opacity: 0.5; }
    .checkmark { width: 18px; height: 18px; border: 2px solid #ddd; border-radius: 3px; position: relative; }
    .checkbox-label input[type="checkbox"]:checked + .checkmark { background: #1976d2; border-color: #1976d2; }
    .checkbox-label input[type="checkbox"]:checked + .checkmark::after { 
      content: '✓'; 
      position: absolute; 
      top: -2px; 
      left: 2px; 
      color: white; 
      font-size: 12px; 
      font-weight: bold; 
    }
    .key-input { display: flex; gap: 10px; }
    .key-input .form-control { flex: 1; font-family: monospace; }
    .settings-actions { display: flex; justify-content: flex-end; gap: 15px; margin-top: 30px; }
    .btn { padding: 10px 20px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-outline { background: white; color: #666; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class StreamSettingsComponent implements OnInit {
  @Input() tournamentId!: number | string;
  
  settings: StreamSettings = {
    tournamentId: 0,
    quality: StreamQuality.HIGH,
    maxViewers: 5000,
    chatEnabled: true,
    moderationEnabled: true,
    recordingEnabled: false,
    autoStart: false,
    streamKey: ''
  };

  saving = false;

  ngOnInit() {
    this.settings.tournamentId = Number(this.tournamentId) || 0;
    this.generateKey();
    this.loadSettings();
  }

  loadSettings() {
    // Mock loading settings
    // In real app, load from backend
  }

  saveSettings() {
    this.saving = true;
    
    // Mock save operation
    setTimeout(() => {
      this.saving = false;
      alert('Configuración guardada exitosamente');
    }, 1000);
  }

  resetSettings() {
    if (confirm('¿Estás seguro de restablecer la configuración?')) {
      this.settings = {
        tournamentId: Number(this.tournamentId) || 0,
        quality: StreamQuality.HIGH,
        maxViewers: 5000,
        chatEnabled: true,
        moderationEnabled: true,
        recordingEnabled: false,
        autoStart: false,
        streamKey: this.generateStreamKey()
      };
    }
  }

  generateKey() {
    this.settings.streamKey = this.generateStreamKey();
  }

  private generateStreamKey(): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < 32; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  }
}