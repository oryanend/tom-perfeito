import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Music } from '../../../shared/models/music';
import { MusicPage } from '../../../shared/models/music-page';
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

  getMusicByUserId(userId: string, page = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(`${this.API}/user/${userId}?page=${page}&size=5`);
  }

  searchMusicByName(name: string, page = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(`${this.API}?name=${name}&page=${page}&size=5`);
  }

  getById(id: string) {
    return this.http.get<MusicPage>(`${this.API}/${id}`);
  }
}
