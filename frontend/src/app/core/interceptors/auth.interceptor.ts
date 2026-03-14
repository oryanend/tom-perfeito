import { Injectable, inject } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthServiceService } from '../services/AuthService/auth-service.service';

@Injectable()
export class AuthErrorInterceptor implements HttpInterceptor {
  private authService = inject(AuthServiceService);

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: unknown) => {
        const httpError = error as HttpErrorResponse;

        if (httpError.status === 0) {
          console.error('Backend offline');
          this.authService.logout();
        }

        if (httpError.status === 401 || httpError.status === 403) {
          console.error('Token inválido');
          this.authService.logout();
        }

        return throwError(() => httpError);
      })
    );
  }
}
