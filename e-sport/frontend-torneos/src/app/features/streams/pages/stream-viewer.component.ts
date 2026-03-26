import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TournamentsService } from '../../tournaments/services/tournaments.service';
import { Tournament } from '../../../core/models/tournament.models';

@Component({
  selector: 'app-stream-viewer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="stream-layout">
      <!-- Main Stream Area -->
      <div class="main-content">
        <div class="video-player">
          <div class="video-container" [ngClass]="{'live': isLive(), 'offline': !isLive()}">
            <div *ngIf="!isLive()" class="offline-overlay">
              <div class="offline-content">
                <div class="tournament-logo"></div>
                <h2>{{tournament?.name || 'Torneo'}}</h2>
                <p class="offline-message">{{getOfflineMessage()}}</p>
                <div class="countdown" *ngIf="tournament">
                  <span>Inicia en: {{formatDateTime(tournament.startDate)}}</span>
                </div>
              </div>
            </div>
            
            <div *ngIf="isLive()" class="live-stream">
              <div class="live-indicator">
                <span class="live-dot"></span>
                <span>EN VIVO</span>
              </div>
              <div class="stream-overlay">
                <div class="game-simulation">
                  <div class="player player-1">
                    <div class="player-avatar"></div>
                    <div class="player-info">
                      <div class="player-name">Jugador 1</div>
                      <div class="player-score">{{player1Score}}</div>
                    </div>
                  </div>
                  <div class="vs-indicator">VS</div>
                  <div class="player player-2">
                    <div class="player-avatar"></div>
                    <div class="player-info">
                      <div class="player-name">Jugador 2</div>
                      <div class="player-score">{{player2Score}}</div>
                    </div>
                  </div>
                </div>
                <div class="match-timer">{{matchTime}}</div>
              </div>
            </div>
            
            <!-- Stream Controls -->
            <div class="stream-controls">
              <button class="control-btn" [class.active]="isMuted" (click)="toggleMute()">
                {{isMuted ? '' : ''}}
              </button>
              <div class="volume-slider">
                <input type="range" min="0" max="100" [(ngModel)]="volume" class="volume-input">
              </div>
              <button class="control-btn" (click)="toggleQuality()">
                 {{currentQuality}}
              </button>
              <button class="control-btn" (click)="toggleFullscreen()">
                ⛶
              </button>
            </div>
          </div>
        </div>
        
        <!-- Stream Info -->
        <div class="stream-info" *ngIf="tournament">
          <div class="stream-header">
            <h1>{{tournament.name}}</h1>
            <div class="stream-stats">
              <span class="viewer-count"> {{viewerCount}} espectadores</span>
              <span class="category">{{tournament.category?.name}}</span>
              <span class="game"> {{tournament.gameType?.name}}</span>
            </div>
          </div>
          <p class="stream-description">{{tournament.description}}</p>
        </div>
      </div>
      
      <!-- Chat Sidebar -->
      <div class="chat-sidebar">
        <div class="chat-header">
          <h3>Chat del Stream</h3>
          <span class="chat-viewers">{{chatUsers}} usuarios</span>
        </div>
        
        <div class="chat-messages">
          <div *ngFor="let message of chatMessages" class="chat-message">
            <span class="username" [style.color]="message.color">{{message.username}}:</span>
            <span class="message-text">{{message.text}}</span>
          </div>
        </div>
        
        <div class="chat-input">
          <input type="text" placeholder="Escribe un mensaje..." 
                 [(ngModel)]="newMessage" 
                 (keyup.enter)="sendMessage()"
                 class="message-input">
          <button (click)="sendMessage()" class="send-btn"></button>
        </div>
      </div>
    </div>
    
    <!-- Back Button -->
    <div class="back-button">
      <button class="btn btn-primary" (click)="goBack()">
        ← Volver a Torneos
      </button>
    </div>
  `,
  styles: [`
    .stream-layout { display: flex; height: 100vh; background: #0f0f23; color: white; }
    .main-content { flex: 1; display: flex; flex-direction: column; }
    .video-player { flex: 1; position: relative; }
    .video-container { width: 100%; height: 100%; position: relative; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
    .video-container.live { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
    .video-container.offline { background: linear-gradient(135deg, #434343 0%, #000000 100%); }
    
    .offline-overlay { position: absolute; top: 0; left: 0; right: 0; bottom: 0; display: flex; align-items: center; justify-content: center; }
    .offline-content { text-align: center; }
    .tournament-logo { font-size: 80px; margin-bottom: 20px; }
    .offline-content h2 { margin: 0 0 15px 0; font-size: 2.5em; }
    .offline-message { font-size: 1.2em; margin-bottom: 20px; opacity: 0.8; }
    .countdown { background: rgba(0,0,0,0.5); padding: 10px 20px; border-radius: 20px; }
    
    .live-stream { position: relative; width: 100%; height: 100%; }
    .live-indicator { position: absolute; top: 20px; left: 20px; background: #ff0000; padding: 8px 15px; border-radius: 20px; font-weight: bold; z-index: 10; }
    .live-dot { display: inline-block; width: 8px; height: 8px; background: white; border-radius: 50%; margin-right: 8px; animation: pulse 2s infinite; }
    
    .stream-overlay { position: absolute; bottom: 80px; left: 50%; transform: translateX(-50%); background: rgba(0,0,0,0.8); padding: 20px; border-radius: 10px; }
    .game-simulation { display: flex; align-items: center; gap: 30px; }
    .player { display: flex; align-items: center; gap: 10px; }
    .player-avatar { font-size: 40px; }
    .player-name { font-weight: bold; }
    .player-score { font-size: 24px; color: #00ff00; }
    .vs-indicator { font-size: 20px; font-weight: bold; color: #ff6b6b; }
    .match-timer { text-align: center; margin-top: 10px; font-size: 18px; color: #ffd93d; }
    
    .stream-controls { position: absolute; bottom: 0; left: 0; right: 0; background: rgba(0,0,0,0.8); padding: 15px; display: flex; align-items: center; gap: 15px; }
    .control-btn { background: none; border: none; color: white; font-size: 18px; cursor: pointer; padding: 8px; border-radius: 4px; transition: background 0.3s; }
    .control-btn:hover, .control-btn.active { background: rgba(255,255,255,0.2); }
    .volume-slider { flex: 1; max-width: 100px; }
    .volume-input { width: 100%; }
    
    .stream-info { padding: 20px; background: #1a1a2e; }
    .stream-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
    .stream-header h1 { margin: 0; font-size: 1.8em; }
    .stream-stats { display: flex; gap: 20px; font-size: 14px; }
    .stream-stats span { background: rgba(255,255,255,0.1); padding: 4px 8px; border-radius: 12px; }
    .stream-description { margin: 0; opacity: 0.8; }
    
    .chat-sidebar { width: 350px; background: #16213e; display: flex; flex-direction: column; }
    .chat-header { padding: 15px; border-bottom: 1px solid #2a3f5f; display: flex; justify-content: space-between; align-items: center; }
    .chat-header h3 { margin: 0; }
    .chat-viewers { font-size: 12px; opacity: 0.7; }
    
    .chat-messages { flex: 1; padding: 10px; overflow-y: auto; max-height: calc(100vh - 120px); }
    .chat-message { margin-bottom: 8px; line-height: 1.4; }
    .username { font-weight: bold; margin-right: 5px; }
    .message-text { word-wrap: break-word; }
    
    .chat-input { padding: 15px; border-top: 1px solid #2a3f5f; display: flex; gap: 10px; }
    .message-input { flex: 1; background: #0f1419; border: 1px solid #2a3f5f; color: white; padding: 8px 12px; border-radius: 20px; }
    .message-input:focus { outline: none; border-color: #667eea; }
    .send-btn { background: #667eea; border: none; color: white; padding: 8px 12px; border-radius: 20px; cursor: pointer; }
    
    .back-button { position: fixed; top: 20px; left: 20px; z-index: 1000; }
    .btn { padding: 10px 20px; border: none; border-radius: 6px; cursor: pointer; font-weight: bold; }
    .btn-primary { background: #667eea; color: white; }
    
    @keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.5; } }
    
    @media (max-width: 768px) {
      .stream-layout { flex-direction: column; }
      .chat-sidebar { width: 100%; height: 300px; }
    }
  `]
})
export class StreamViewerComponent implements OnInit, OnDestroy {
  tournament: Tournament | null = null;
  loading = false;
  error: string | null = null;
  
  // Stream state
  viewerCount = 0;
  chatUsers = 0;
  isMuted = false;
  volume = 75;
  currentQuality = '1080p';
  
  // Game simulation
  player1Score = 0;
  player2Score = 0;
  matchTime = '00:00';
  
  // Chat
  chatMessages: any[] = [];
  newMessage = '';
  
  private gameInterval: any;
  private chatInterval: any;
  private timeInterval: any;
  private startTime = Date.now();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tournamentsService: TournamentsService
  ) {}

  ngOnInit() {
    const tournamentId = this.route.snapshot.paramMap.get('tournamentId');
    if (tournamentId) {
      this.loadTournament(tournamentId);
      this.initializeStream();
    }
  }

  ngOnDestroy() {
    if (this.gameInterval) clearInterval(this.gameInterval);
    if (this.chatInterval) clearInterval(this.chatInterval);
    if (this.timeInterval) clearInterval(this.timeInterval);
  }

  loadTournament(tournamentId: string) {
    this.loading = true;
    this.tournamentsService.getTournament(Number(tournamentId)).subscribe({
      next: (tournament) => {
        this.tournament = tournament;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar el torneo';
        this.loading = false;
      }
    });
  }

  initializeStream() {
    // Simulate viewer count
    this.viewerCount = Math.floor(Math.random() * 5000) + 100;
    this.chatUsers = Math.floor(this.viewerCount * 0.1);
    
    // Add initial chat messages
    this.addInitialChatMessages();
    
    // Start intervals for live simulation
    if (this.isLive()) {
      this.startGameSimulation();
      this.startChatSimulation();
      this.startTimer();
    }
  }

  isLive(): boolean {
    return this.tournament?.status === 'IN_PROGRESS';
  }

  getOfflineMessage(): string {
    if (!this.tournament) return 'Stream no disponible';
    
    const now = new Date();
    const startDate = new Date(this.tournament.startDate);
    
    if (startDate > now) {
      return 'El stream comenzará cuando inicie el torneo';
    } else if (this.tournament.status === 'COMPLETED') {
      return 'El torneo ha finalizado';
    }
    return 'El stream no está disponible en este momento';
  }

  startGameSimulation() {
    this.gameInterval = setInterval(() => {
      if (Math.random() > 0.7) {
        if (Math.random() > 0.5) {
          this.player1Score++;
        } else {
          this.player2Score++;
        }
      }
      
      // Simulate viewer count changes
      this.viewerCount += Math.floor(Math.random() * 20) - 10;
      this.viewerCount = Math.max(50, this.viewerCount);
    }, 3000);
  }

  startChatSimulation() {
    const sampleMessages = [
      'GG!', '¡Increíble jugada!', 'Vamos equipo azul!', 'Epic fail ',
      'OMG que partida', '¡Qué pro!', 'Nooo casi!', 'POGGERS',
      'Clutch time!', '¡Vamos que se puede!', 'F en el chat',
      'Mejor jugador del mundo', '¡Qué tensión!', 'EZ Clap'
    ];
    
    this.chatInterval = setInterval(() => {
      const message = {
        username: `Usuario${Math.floor(Math.random() * 1000)}`,
        text: sampleMessages[Math.floor(Math.random() * sampleMessages.length)],
        color: this.getRandomColor()
      };
      
      this.chatMessages.push(message);
      if (this.chatMessages.length > 50) {
        this.chatMessages.shift();
      }
    }, 2000 + Math.random() * 3000);
  }

  startTimer() {
    this.timeInterval = setInterval(() => {
      const elapsed = Math.floor((Date.now() - this.startTime) / 1000);
      const minutes = Math.floor(elapsed / 60);
      const seconds = elapsed % 60;
      this.matchTime = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    }, 1000);
  }

  addInitialChatMessages() {
    const initialMessages = [
      { username: 'Moderador', text: '¡Bienvenidos al stream!', color: '#ff6b6b' },
      { username: 'ProGamer123', text: 'Hype!', color: '#4ecdc4' },
      { username: 'StreamFan', text: '¡Por fin empieza!', color: '#45b7d1' }
    ];
    
    this.chatMessages = [...initialMessages];
  }

  getRandomColor(): string {
    const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff'];
    return colors[Math.floor(Math.random() * colors.length)];
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      this.chatMessages.push({
        username: 'Tú',
        text: this.newMessage,
        color: '#ffd93d'
      });
      this.newMessage = '';
      
      if (this.chatMessages.length > 50) {
        this.chatMessages.shift();
      }
    }
  }

  toggleMute() {
    this.isMuted = !this.isMuted;
  }

  toggleQuality() {
    const qualities = ['720p', '1080p', '1440p', '4K'];
    const currentIndex = qualities.indexOf(this.currentQuality);
    this.currentQuality = qualities[(currentIndex + 1) % qualities.length];
  }

  toggleFullscreen() {
    // Simulate fullscreen toggle
    alert('Modo pantalla completa activado');
  }

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleString('es-ES');
  }

  goBack() {
    this.router.navigate(['/tournaments']);
  }
}