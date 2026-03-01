import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Chord} from '../../models/chord';

@Injectable({
  providedIn: 'root'
})
export class ChordService {

  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  searchByNotes(notes: string[]) {
    let params = new HttpParams();

    notes.forEach(note => {
      params = params.append('notes', note);
    });

    return this.http.get<Chord[]>(
      `${this.apiUrl}/chords/search`,
      { params }
    );
  }
}
