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

  getAllMusics(page = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(`${environment.apiUrl}/musics?page=${page}&size=5`);
  }
}
