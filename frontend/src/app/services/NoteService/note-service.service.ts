import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note } from '../../models/note';


@Injectable({
  providedIn: 'root'
})
export class NoteServiceService {

  private apiUrl = 'http://localhost:8080';
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
