import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Chord } from '../../../shared/models/chord';
import { environment } from '../../../../environments/environment';
import { PageResponse } from '../../../shared/models/page-response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ChordService {
  private apiUrl = environment.apiUrl;
  private http = inject(HttpClient);

  searchByNotes(notes: string[]) {
    let params = new HttpParams();

    notes.forEach((note) => {
      params = params.append('notes', note);
    });

    return this.http.get<Chord[]>(`${this.apiUrl}/chords/search`, { params });
  }

  searchByName(name: string) {
    const params = new HttpParams().set('name', name);

    return this.http.get<Chord[]>(`${this.apiUrl}/chords/search`, { params });
  }

  getAll(page = 0): Observable<PageResponse<Chord>> {
    return this.http.get<PageResponse<Chord>>(`${this.apiUrl}/chords?page=${page}&size=5`);
  }
}
