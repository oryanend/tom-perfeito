import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { LoginResponse } from '../login-response';
import { User } from '../../../shared/models/user';

@Injectable({
  providedIn: 'root',
})
export class AuthServiceService {
  private http = inject(HttpClient);
  private userSubject = new BehaviorSubject<User | null>(null);
  private tokenExpirationTimer: ReturnType<typeof setTimeout> | null = null;

  user$ = this.userSubject.asObservable();

  constructor() {
    this.restoreUser();
  }

  register(userData: { username: string; email: string; password: string }) {
    return this.http.post(`${environment.apiUrl}/auth/register`, userData);
  }

  login(credentials: { email: string; password: string }) {
    const body = new HttpParams()
      .set('email', credentials.email)
      .set('password', credentials.password)
      .set('grant_type', 'password');

    const basicAuth = btoa(`myclientid:myclientsecret`);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: `Basic ${basicAuth}`,
    });

    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, body.toString(), {
      headers,
    });
  }

  saveToken(token: string) {
    localStorage.setItem('access_token', token);

    try {
      const decoded = jwtDecode(token) as { exp?: number };

      if (decoded.exp) {
        const expMs = decoded.exp * 1000;
        localStorage.setItem('token_exp', String(expMs));
        this.scheduleAutoLogout(new Date(expMs));
      }

      this.loadUserFromApi();
    } catch {
      this.logout();
    }
  }

  logout() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('token_exp');
    this.userSubject.next(null);

    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
      this.tokenExpirationTimer = null;
    }
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('access_token');
    const exp = localStorage.getItem('token_exp');

    if (!token || !exp) return false;

    return Date.now() < Number(exp);
  }

  private scheduleAutoLogout(expirationDate: Date) {
    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
    }

    const millis = expirationDate.getTime() - Date.now();

    if (millis <= 0) {
      this.logout();
      return;
    }

    this.tokenExpirationTimer = setTimeout(() => {
      this.logout();
    }, millis);
  }

  private restoreUser() {
    const token = localStorage.getItem('access_token');

    if (!token) {
      this.userSubject.next(null);
      return;
    }

    try {
      const decoded = jwtDecode(token) as { exp?: number };

      if (decoded.exp) {
        const expMs = decoded.exp * 1000;

        if (Date.now() > expMs) {
          this.logout();
          return;
        }

        this.scheduleAutoLogout(new Date(expMs));
      }
      this.loadUserFromApi();
    } catch {
      this.logout();
    }
  }

  private loadUserFromApi() {
    const token = localStorage.getItem('access_token');

    if (!token) {
      this.userSubject.next(null);
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.http.get<User>(`${environment.apiUrl}/users/me`, { headers }).subscribe({
      next: (user) => {
        this.userSubject.next(user);
      },
      error: () => {
        this.userSubject.next(null);
      },
    });
  }

  restoreSession() {
    this.restoreUser();
  }
}
