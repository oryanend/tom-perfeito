import { Injectable } from '@angular/core';
import {User} from '../../models/user';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) {}

  getMe() {
    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    return this.http.get<User>(`${environment.apiUrl}/users/me`, { headers });
  }

  updateFirstLogin(value: boolean) {
    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    return this.http.patch(
      `${environment.apiUrl}/users/me`,
      { isFirstLogin: value },
      { headers }
    );
  }
}
