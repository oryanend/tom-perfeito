import { HttpInterceptorFn } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {AuthServiceService} from '../services/AuthService/auth-service.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('access_token');
  if (!token) {
    return next(req);
  }

  const authReq = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`)
  });
  return next(authReq);
};

@Injectable()
export class AuthErrorInterceptor implements HttpInterceptor {

  constructor(private authService: AuthServiceService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {

        // backend desligado
        if (error.status === 0) {
          console.error('Backend offline');
          this.authService.logout();
        }

        // token inválido ou expirado
        if (error.status === 401 || error.status === 403) {
          console.error('Token inválido');
          this.authService.logout();
        }

        return throwError(() => error);
      })
    );
  }
}
