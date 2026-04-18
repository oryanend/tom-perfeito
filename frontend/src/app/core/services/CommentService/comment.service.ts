import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Comment } from '../../../shared/models/comment';
import { PageResponse } from '../../../shared/models/page-response';
import { AuthError } from '../../errors/auth/auth-error';
import { CommentMin } from '../../../shared/models/comment-min';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private ApiUrl = `${environment.apiUrl}/musics`;
  private http = inject(HttpClient);

  getCommentByMusic(musicId: string, page = 0): Observable<PageResponse<Comment>> {
    return this.http.get<PageResponse<Comment>>(
      `${this.ApiUrl}/${musicId}/comments?page=${page}&size=10`
    );
  }

  getCommentByUserId(userId: string, page = 0): Observable<PageResponse<CommentMin>> {
    return this.http.get<PageResponse<CommentMin>>(
      `${environment.apiUrl}/users/${userId}/comments?page=${page}&size=4`
    );
  }

  insertCommentByMusic(musicId: string, body: string, parentId?: number): Observable<Comment> {
    const token = localStorage.getItem('access_token');

    if (!token) {
      return throwError(() => new AuthError());
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });

    return this.http.post<Comment>(
      `${this.ApiUrl}/${musicId}/comments`,
      { body, parentId: parentId ?? null },
      { headers }
    );
  }
}
