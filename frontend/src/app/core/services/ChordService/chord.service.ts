import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Chord } from '../../../shared/models/chord';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ChordService {
  private apiUrl = environment.apiUrl;

  // use inject() instead of constructor injection to satisfy lint
  private http = inject(HttpClient);

  searchByNotes(notes: string[]) {
    let params = new HttpParams();

    notes.forEach((note) => {
      params = params.append('notes', note);
    });

    return this.http.get<Chord[]>(`${this.apiUrl}/chords/search`, { params });
  }
}
