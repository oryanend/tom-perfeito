import { inject, Injectable } from '@angular/core';
import { User } from '../../../shared/models/user';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);

  getMe() {
    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    return this.http.get<User>(`${environment.apiUrl}/users/me`, { headers });
  }

  getUserById(id: string) {
    return this.http.get<User>(`${environment.apiUrl}/users/${id}`);
  }

  updateFirstLogin() {
    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    return this.http.patch(`${environment.apiUrl}/users/me/first-login`, {}, { headers });
  }

  getFirstLogin() {
    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    return this.http
      .get<User>(`${environment.apiUrl}/users/me`, { headers })
      .pipe(map((user) => user.firstLogin));
  }
}
