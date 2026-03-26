import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment } from '../../../shared/models/comment';
import { PageResponse } from '../../../shared/models/page-response';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private ApiUrl = `${environment.apiUrl}/musics`;

  constructor(private http: HttpClient) {}

  getCommentByMusic(musicId: string): Observable<PageResponse<Comment>> {
    return this.http.get<PageResponse<Comment>>(`${this.ApiUrl}/${musicId}/comments`);
  }

  insertCommentByMusic(musicId: string, body: string, parentId?: number): Observable<Comment> {
    const token = localStorage.getItem('access_token');

    if (!token) {
      console.error('Token não encontrado! Usuário pode não estar logado.');
      throw new Error('Token não encontrado');
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.http.post<Comment>(
      `${this.ApiUrl}/${musicId}/comments`,
      { body, parentId: parentId || null },
      { headers }
    );
  }
}
