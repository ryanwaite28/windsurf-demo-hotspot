import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PagedData } from '../models/api.models';
import {
  CreateThreadRequest,
  EditThreadRequest,
  QuoteThreadRequest,
  ReactRequest,
  ReportRequest,
  Thread,
} from '../models/social.models';

@Injectable({ providedIn: 'root' })
export class ThreadService {
  private readonly apiUrl = `${environment.apiBaseUrl}/social`;

  constructor(private http: HttpClient) {}

  createThread(request: CreateThreadRequest): Observable<ApiResponse<Thread>> {
    return this.http.post<ApiResponse<Thread>>(`${this.apiUrl}/threads`, request);
  }

  getThread(threadId: string): Observable<ApiResponse<Thread>> {
    return this.http.get<ApiResponse<Thread>>(`${this.apiUrl}/threads/${threadId}`);
  }

  editThread(threadId: string, request: EditThreadRequest): Observable<ApiResponse<Thread>> {
    return this.http.put<ApiResponse<Thread>>(`${this.apiUrl}/threads/${threadId}`, request);
  }

  deleteThread(threadId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}`);
  }

  repostThread(threadId: string): Observable<ApiResponse<Thread>> {
    return this.http.post<ApiResponse<Thread>>(`${this.apiUrl}/threads/${threadId}/repost`, {});
  }

  quoteThread(threadId: string, request: QuoteThreadRequest): Observable<ApiResponse<Thread>> {
    return this.http.post<ApiResponse<Thread>>(`${this.apiUrl}/threads/${threadId}/quote`, request);
  }

  togglePin(threadId: string): Observable<ApiResponse<Thread>> {
    return this.http.put<ApiResponse<Thread>>(`${this.apiUrl}/threads/${threadId}/pin`, {});
  }

  likeThread(threadId: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/like`, {});
  }

  unlikeThread(threadId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/like`);
  }

  reactToThread(threadId: string, request: ReactRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/react`, request);
  }

  removeReaction(threadId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/react`);
  }

  saveThread(threadId: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/save`, {});
  }

  unsaveThread(threadId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/save`);
  }

  reportThread(threadId: string, request: ReportRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/report`, request);
  }

  muteThread(threadId: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/mute`, {});
  }

  unmuteThread(threadId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/threads/${threadId}/mute`);
  }

  getUserThreads(userId: string, page = 0, size = 20): Observable<ApiResponse<PagedData<Thread>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PagedData<Thread>>>(`${this.apiUrl}/users/${userId}/threads`, { params });
  }

  getUserSavedThreads(userId: string, page = 0, size = 20): Observable<ApiResponse<PagedData<Thread>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PagedData<Thread>>>(`${this.apiUrl}/users/${userId}/saved`, { params });
  }
}
