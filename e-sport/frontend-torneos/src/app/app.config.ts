import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { routes } from './app.routes';
import { TokenService } from './core/services/token.service';

const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Skip adding token to auth endpoints
  if (req.url.includes('/auth/')) {
    return next(req);
  }
  
  const tokenService = inject(TokenService);
  const token = tokenService.getAccessToken();
  
  if (token) {
    let headers = req.headers.set('Authorization', `Bearer ${token}`);
    
    // Extract user ID from token and add X-USER-ID header
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload.sub) {
        headers = headers.set('X-USER-ID', payload.sub);
      }
    } catch (e) {
      console.error('Error parsing token for user ID:', e);
    }
    
    const authReq = req.clone({ headers });
    return next(authReq);
  }
  
  return next(req);
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};