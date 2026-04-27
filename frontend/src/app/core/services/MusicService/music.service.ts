import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Music } from '../../../shared/models/music';
import { MusicPage } from '../../../shared/models/music-page';
import { PageResponse } from '../../../shared/models/page-response';
import { Lyric } from '../../../shared/models/lyric';
import { AuthError } from '../../errors/auth/auth-error';
import { MusicUpdate } from '../../../shared/models/music-update';

@Injectable({
  providedIn: 'root',
})
export class MusicService {
  private http = inject(HttpClient);
  private API = `${environment.apiUrl}/musics`;

  getAllMusics(page = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(`${this.API}?page=${page}&size=5`);
  }

  getMusicByUserId(userId: string, page = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(`${this.API}/user/${userId}?page=${page}&size=4`);
  }

  searchMusicByName(name: string, page = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(`${this.API}?name=${name}&page=${page}&size=5`);
  }

  getById(id: string) {
    return this.http.get<MusicPage>(`${this.API}/${id}`);
  }

  createMusic(
    music: { title: string; description: string; releaseDate: string; lyric: Lyric },
    token: string
  ) {
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
    return this.http.post<MusicPage>(this.API, music, { headers });
  }

  updateMusic(id: string, music: MusicUpdate): Observable<MusicPage> {
    const token = localStorage.getItem('access_token');

    if (!token) {
      return throwError(() => new AuthError());
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.http.patch<MusicPage>(`${this.API}/${id}`, music, { headers });
  }
}
