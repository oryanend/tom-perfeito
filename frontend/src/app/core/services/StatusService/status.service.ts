import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import { Status } from '../../../shared/models/status';

@Injectable({
  providedIn: 'root',
})
export class StatusService {
  // use inject() instead of constructor injection to satisfy lint
  private http = inject(HttpClient);

  getStatus(): Observable<Status> {
    return this.http.get<Status>(`${environment.apiUrl}/status`);
  }
}
