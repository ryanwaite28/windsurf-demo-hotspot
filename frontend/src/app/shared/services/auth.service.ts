import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ApiResponse,
  AuthResponse,
  LoginRequest,
  PasswordResetRequest,
  SignupRequest,
  UserInfo,
} from '../models/api.models';

const TOKEN_KEY = 'hotspot_access_token';
const REFRESH_TOKEN_KEY = 'hotspot_refresh_token';
const USER_KEY = 'hotspot_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(this.getStoredUser());

  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  get isLoggedIn(): boolean {
    return !!this.getAccessToken();
  }

  get currentUser(): UserInfo | null {
    return this.currentUserSubject.value;
  }

  signup(request: SignupRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.apiUrl}/signup`, request)
      .pipe(tap((res) => this.handleAuthResponse(res.data)));
  }

  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, request)
      .pipe(tap((res) => this.handleAuthResponse(res.data)));
  }

  logout(): Observable<ApiResponse<void>> {
    return this.http
      .post<ApiResponse<void>>(`${this.apiUrl}/logout`, {})
      .pipe(tap(() => this.clearAuth()));
  }

  refreshToken(): Observable<ApiResponse<AuthResponse>> {
    const refreshToken = this.getRefreshToken();
    return this.http
      .post<ApiResponse<AuthResponse>>(`${this.apiUrl}/refresh`, { refreshToken })
      .pipe(tap((res) => this.handleAuthResponse(res.data)));
  }

  requestPasswordReset(request: PasswordResetRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/password-reset/request`, request);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  clearAuth(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUserSubject.next(null);
  }

  private handleAuthResponse(data: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, data.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(data.user));
    this.currentUserSubject.next(data.user);
  }

  private getStoredUser(): UserInfo | null {
    const stored = localStorage.getItem(USER_KEY);
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {
        return null;
      }
    }
    return null;
  }
}
