import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  FollowUser,
  PagedData,
  PrivacySettings,
  SecuritySettings,
  UpdatePrivacyRequest,
  UpdateProfileRequest,
  UpdateSecurityRequest,
  UserProfile,
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly apiUrl = `${environment.apiBaseUrl}/users`;

  constructor(private http: HttpClient) {}

  getUserProfile(username: string): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.apiUrl}/${username}`);
  }

  updateProfile(request: UpdateProfileRequest): Observable<ApiResponse<UserProfile>> {
    return this.http.put<ApiResponse<UserProfile>>(`${this.apiUrl}/me`, request);
  }

  deleteAccount(): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/me`);
  }

  getPrivacySettings(): Observable<ApiResponse<PrivacySettings>> {
    return this.http.get<ApiResponse<PrivacySettings>>(`${this.apiUrl}/me/settings/privacy`);
  }

  updatePrivacySettings(request: UpdatePrivacyRequest): Observable<ApiResponse<PrivacySettings>> {
    return this.http.put<ApiResponse<PrivacySettings>>(`${this.apiUrl}/me/settings/privacy`, request);
  }

  getSecuritySettings(): Observable<ApiResponse<SecuritySettings>> {
    return this.http.get<ApiResponse<SecuritySettings>>(`${this.apiUrl}/me/settings/security`);
  }

  updateSecuritySettings(request: UpdateSecurityRequest): Observable<ApiResponse<SecuritySettings>> {
    return this.http.put<ApiResponse<SecuritySettings>>(`${this.apiUrl}/me/settings/security`, request);
  }

  followUser(userId: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/${userId}/follow`, {});
  }

  unfollowUser(userId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${userId}/follow`);
  }

  muteUser(userId: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/${userId}/mute`, {});
  }

  unmuteUser(userId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${userId}/mute`);
  }

  blockUser(userId: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/${userId}/block`, {});
  }

  unblockUser(userId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${userId}/block`);
  }

  reportUser(userId: string, reason: string, details: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/${userId}/report`, { reason, details });
  }

  getFollowers(userId: string, page = 0, size = 20): Observable<ApiResponse<PagedData<FollowUser>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PagedData<FollowUser>>>(`${this.apiUrl}/${userId}/followers`, { params });
  }

  getFollowing(userId: string, page = 0, size = 20): Observable<ApiResponse<PagedData<FollowUser>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PagedData<FollowUser>>>(`${this.apiUrl}/${userId}/following`, { params });
  }
}
