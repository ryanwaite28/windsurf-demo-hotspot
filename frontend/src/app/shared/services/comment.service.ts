import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PagedData } from '../models/api.models';
import {
  CreateCommentRequest,
  EditCommentRequest,
  ReportRequest,
  ThreadComment,
} from '../models/social.models';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private readonly apiUrl = `${environment.apiBaseUrl}/social`;

  constructor(private http: HttpClient) {}

  createComment(threadId: string, request: CreateCommentRequest): Observable<ApiResponse<ThreadComment>> {
    return this.http.post<ApiResponse<ThreadComment>>(`${this.apiUrl}/threads/${threadId}/comments`, request);
  }

  getComments(threadId: string, page = 0, size = 20): Observable<ApiResponse<PagedData<ThreadComment>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PagedData<ThreadComment>>>(`${this.apiUrl}/threads/${threadId}/comments`, { params });
  }

  editComment(commentId: string, request: EditCommentRequest): Observable<ApiResponse<ThreadComment>> {
    return this.http.put<ApiResponse<ThreadComment>>(`${this.apiUrl}/comments/${commentId}`, request);
  }

  deleteComment(commentId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/comments/${commentId}`);
  }

  reportComment(commentId: string, request: ReportRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/comments/${commentId}/report`, request);
  }

  muteComment(commentId: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/comments/${commentId}/mute`, {});
  }

  unmuteComment(commentId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/comments/${commentId}/mute`);
  }
}
