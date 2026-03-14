import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note } from '../../../shared/models/note';
import {environment} from '../../../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class NoteServiceService {

  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) {}

  findAll(): Observable<Note[]> {
    return this.http.get<Note[]>(this.apiUrl);
  }

  searchNote(name: string, accidental: string): Observable<Note> {
    return this.http.get<Note>(`${this.apiUrl}/notes/search`, {
      params: { name, accidental }
    });
  }
}
