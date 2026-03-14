import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {Music} from '../../../shared/models/music';
import {PageResponse} from '../../../shared/models/page-response';

@Injectable({
  providedIn: 'root'
})
export class MusicService {

  constructor(private http: HttpClient) {}

  getAllMusics(page: number = 0): Observable<PageResponse<Music>> {
    return this.http.get<PageResponse<Music>>(
      `${environment.apiUrl}/musics?page=${page}&size=5`
    );
  }
}
