import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenService } from '../services/token.service';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private tokenService: TokenService,
    private authService: AuthService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenService.getAccessToken();
    const currentUser = this.authService.getCurrentUser();
    
    console.log('Interceptor - Token:', !!token);
    console.log('Interceptor - Current user:', currentUser);
    
    if (token) {
      let headers = req.headers.set('Authorization', `Bearer ${token}`);
      
      // Add X-USER-ID header - try different approaches
      if (currentUser?.id) {
        headers = headers.set('X-USER-ID', currentUser.id.toString());
        console.log('Added X-USER-ID:', currentUser.id);
      } else {
        // Fallback: extract from token
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          if (payload.sub) {
            headers = headers.set('X-USER-ID', payload.sub);
            console.log('Added X-USER-ID from token:', payload.sub);
          }
        } catch (e) {
          console.error('Error parsing token for user ID:', e);
        }
      }
      
      const authReq = req.clone({ headers });
      console.log('Request headers:', authReq.headers.keys());
      return next.handle(authReq);
    }
    
    return next.handle(req);
  }
}