import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import {LoginResponse} from '../login-response';

@Injectable({
  providedIn: 'root'
})

export class AuthServiceService {
  private userSubject = new BehaviorSubject<string | null>(null);
  user$ = this.userSubject.asObservable();

  private tokenExpirationTimer: any = null;

  constructor(private http: HttpClient) {
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

    const clientId = 'myclientid';
    const clientSecret = 'myclientsecret';
    const basicAuth = btoa(`${clientId}:${clientSecret}`);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': `Basic ${basicAuth}`
    });

    return this.http.post<LoginResponse>(
      `${environment.apiUrl}/auth/login`,
      body.toString(),
      { headers }
    );
  }

  saveToken(token: string) {
    localStorage.setItem('access_token', token);
    try {
      const decoded: any = jwtDecode(token);

      if (decoded && decoded.exp) {
        const expMs = decoded.exp * 1000;
        localStorage.setItem('token_exp', String(expMs));
        this.scheduleAutoLogout(new Date(expMs));
      }

      const fallbackUser = decoded.username || decoded.sub || null;
      if (fallbackUser) {
        this.userSubject.next(fallbackUser);
      }

      this.loadUserFromApi();
    } catch (e) {
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
    const expMs = Number(exp);
    if (isNaN(expMs)) return false;
    return Date.now() < expMs;
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
      const decoded: any = jwtDecode(token);
      const expMs = decoded && decoded.exp ? decoded.exp * 1000 : null;

      if (expMs && Date.now() > expMs) {
        this.logout();
        return;
      }

      if (expMs) {
        this.scheduleAutoLogout(new Date(expMs));
      }

      const fallbackUser = decoded.username || decoded.sub || null;

      if (fallbackUser) {
        this.userSubject.next(fallbackUser);
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

    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http.get<any>(`${environment.apiUrl}/users/me`, { headers }).subscribe({
      next: (user) => {
        const username = user?.username ?? user?.name ?? (typeof user === 'string' ? user : null);
        this.userSubject.next(username);
      },
      error: () => {
        this.userSubject.next(null);
      }
    });
  }
}
