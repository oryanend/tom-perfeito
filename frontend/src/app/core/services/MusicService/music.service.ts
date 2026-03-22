import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Music } from '../../../shared/models/music';
import { PageResponse } from '../../../shared/models/page-response';

@Injectable({
  providedIn: 'root',
})
export class MusicService {
  // use inject() instead of constructor injection to satisfy lint
  private http = inject(HttpClient);
  private API = `${environment.apiUrl}/musics`;

  getAllMusics(page = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(`${this.API}?page=${page}&size=5`);
  }

  searchMusicByName(name: string, page = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(`${this.API}?name=${name}&page=${page}&size=5`);
  }
}
