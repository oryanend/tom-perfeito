import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})

export class AuthServiceService {

  constructor(private http: HttpClient) { }

  register(userData: { username: string; email: string; password: string }) {
    return this.http.post(`http://localhost:8080/auth/register`, userData);
  }

  login(credentials: { email: string; password: string }) {
    return this.http.post(`http://localhost:8080/auth/login`, credentials);
  }
}
