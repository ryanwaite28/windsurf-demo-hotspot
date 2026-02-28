import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { UserService } from './user.service';
import { environment } from '../../../environments/environment';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiBaseUrl}/users`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user profile', () => {
    const mockProfile = {
      success: true,
      message: 'ok',
      data: {
        id: '1', email: 'test@test.com', username: 'testuser', displayName: 'Test',
        bio: null, profilePictureUrl: null, location: null, phone: null,
        followersCount: 5, followingCount: 3, createdAt: '2026-01-01',
      },
    };

    service.getUserProfile('testuser').subscribe((res) => {
      expect(res.data.username).toBe('testuser');
      expect(res.data.followersCount).toBe(5);
    });

    const req = httpMock.expectOne(`${apiUrl}/testuser`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProfile);
  });

  it('should update profile', () => {
    service.updateProfile({ displayName: 'New Name', bio: 'New bio' }).subscribe((res) => {
      expect(res.data.displayName).toBe('New Name');
    });

    const req = httpMock.expectOne(`${apiUrl}/me`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.displayName).toBe('New Name');
    req.flush({ success: true, message: 'ok', data: { displayName: 'New Name' } });
  });

  it('should delete account', () => {
    service.deleteAccount().subscribe((res) => {
      expect(res.success).toBeTrue();
    });

    const req = httpMock.expectOne(`${apiUrl}/me`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should follow a user', () => {
    service.followUser('user-123').subscribe((res) => {
      expect(res.success).toBeTrue();
    });

    const req = httpMock.expectOne(`${apiUrl}/user-123/follow`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should unfollow a user', () => {
    service.unfollowUser('user-123').subscribe((res) => {
      expect(res.success).toBeTrue();
    });

    const req = httpMock.expectOne(`${apiUrl}/user-123/follow`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should get privacy settings', () => {
    service.getPrivacySettings().subscribe((res) => {
      expect(res.data.profileVisibility).toBe('PUBLIC');
    });

    const req = httpMock.expectOne(`${apiUrl}/me/settings/privacy`);
    expect(req.request.method).toBe('GET');
    req.flush({
      success: true, message: 'ok',
      data: { profileVisibility: 'PUBLIC', postsVisibility: 'PUBLIC', locationVisibility: 'PUBLIC', phoneVisibility: 'NOBODY' },
    });
  });

  it('should update security settings', () => {
    service.updateSecuritySettings({ twoFactorEnabled: true }).subscribe((res) => {
      expect(res.data.twoFactorEnabled).toBeTrue();
    });

    const req = httpMock.expectOne(`${apiUrl}/me/settings/security`);
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, message: 'ok', data: { twoFactorEnabled: true } });
  });

  it('should block a user', () => {
    service.blockUser('user-456').subscribe((res) => {
      expect(res.success).toBeTrue();
    });

    const req = httpMock.expectOne(`${apiUrl}/user-456/block`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should report a user', () => {
    service.reportUser('user-789', 'Spam', 'Posting spam').subscribe((res) => {
      expect(res.success).toBeTrue();
    });

    const req = httpMock.expectOne(`${apiUrl}/user-789/report`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.reason).toBe('Spam');
    req.flush({ success: true, message: 'ok', data: null });
  });
});
