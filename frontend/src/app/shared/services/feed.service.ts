import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PagedData } from '../models/api.models';
import { Thread } from '../models/social.models';

@Injectable({ providedIn: 'root' })
export class FeedService {
  private readonly apiUrl = `${environment.apiBaseUrl}/social`;

  constructor(private http: HttpClient) {}

  getFeed(page = 0, size = 20): Observable<ApiResponse<PagedData<Thread>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PagedData<Thread>>>(`${this.apiUrl}/feed`, { params });
  }
}
