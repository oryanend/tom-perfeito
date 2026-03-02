import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
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

  constructor(private http: HttpClient) {
    this.restoreUser();
  }

  register(userData: { username: string; email: string; password: string }) {
    return this.http.post(`http://localhost:8080/auth/register`, userData);
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
      'http://localhost:8080/auth/login',
      body.toString(),
      { headers }
    );
  }

  saveToken(token: string) {
    localStorage.setItem('access_token', token);
    this.loadUserFromToken();
  }

  logout() {
    localStorage.removeItem('access_token');
    this.userSubject.next(null);
  }

  private loadUserFromToken() {
    const token = localStorage.getItem('access_token');
    if (!token) return;

    const decoded: any = jwtDecode(token);

    this.userSubject.next(decoded.username);
  }

  private restoreUser() {
    const token = localStorage.getItem('access_token');

    if (!token) {
      this.userSubject.next(null);
      return;
    }

    try {
      const decoded: any = jwtDecode(token);

      this.userSubject.next(decoded.username || decoded.sub);

    } catch {
      this.logout();
    }
  }
}
