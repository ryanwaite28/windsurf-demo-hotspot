import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiBaseUrl}/auth`;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return false for isLoggedIn when no token', () => {
    expect(service.isLoggedIn).toBeFalse();
  });

  it('should return true for isLoggedIn when token exists', () => {
    localStorage.setItem('hotspot_access_token', 'test-token');
    expect(service.isLoggedIn).toBeTrue();
  });

  it('should store tokens and user on signup', () => {
    const mockResponse = {
      success: true,
      message: 'ok',
      data: {
        accessToken: 'access-123',
        refreshToken: 'refresh-123',
        tokenType: 'Bearer',
        user: { id: '1', email: 'test@test.com', username: 'testuser', displayName: 'Test' },
      },
    };

    service.signup({
      email: 'test@test.com',
      displayName: 'Test',
      username: 'testuser',
      password: 'Password123!',
      confirmPassword: 'Password123!',
    }).subscribe((res) => {
      expect(res.data.accessToken).toBe('access-123');
      expect(service.isLoggedIn).toBeTrue();
      expect(service.currentUser?.username).toBe('testuser');
    });

    const req = httpMock.expectOne(`${apiUrl}/signup`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should store tokens and user on login', () => {
    const mockResponse = {
      success: true,
      message: 'ok',
      data: {
        accessToken: 'access-456',
        refreshToken: 'refresh-456',
        tokenType: 'Bearer',
        user: { id: '2', email: 'login@test.com', username: 'loginuser', displayName: 'Login' },
      },
    };

    service.login({ email: 'login@test.com', password: 'Password123!' }).subscribe((res) => {
      expect(res.data.accessToken).toBe('access-456');
      expect(service.currentUser?.email).toBe('login@test.com');
    });

    const req = httpMock.expectOne(`${apiUrl}/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should clear auth on logout', () => {
    localStorage.setItem('hotspot_access_token', 'token');
    localStorage.setItem('hotspot_refresh_token', 'refresh');
    localStorage.setItem('hotspot_user', '{"id":"1"}');

    service.logout().subscribe(() => {
      expect(service.isLoggedIn).toBeFalse();
      expect(service.currentUser).toBeNull();
    });

    const req = httpMock.expectOne(`${apiUrl}/logout`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should call password reset endpoint', () => {
    service.requestPasswordReset({ email: 'reset@test.com' }).subscribe((res) => {
      expect(res.success).toBeTrue();
    });

    const req = httpMock.expectOne(`${apiUrl}/password-reset/request`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.email).toBe('reset@test.com');
    req.flush({ success: true, message: 'ok', data: null });
  });
});
