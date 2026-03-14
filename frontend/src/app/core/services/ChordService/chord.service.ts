import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Chord} from '../../../shared/models/chord';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChordService {

  private apiUrl = environment.apiUrl;

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
