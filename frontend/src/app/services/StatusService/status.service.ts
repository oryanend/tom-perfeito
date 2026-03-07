import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';
import { Status } from "../../models/status";

@Injectable({
  providedIn: 'root'
})
export class StatusService {
  constructor(private http: HttpClient) {}

  getStatus(): Observable<Status> {
    return this.http.get<Status>(`${environment.apiUrl}/status`);
  }
}
