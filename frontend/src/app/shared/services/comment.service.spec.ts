import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { CommentService } from './comment.service';
import { environment } from '../../../environments/environment';

describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiBaseUrl}/social`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CommentService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a comment', () => {
    service.createComment('t1', { content: 'Nice!' }).subscribe((res) => {
      expect(res.data.content).toBe('Nice!');
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/comments`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.content).toBe('Nice!');
    req.flush({ success: true, message: 'ok', data: { id: 'c1', content: 'Nice!' } });
  });

  it('should get comments for a thread', () => {
    service.getComments('t1').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });
    const req = httpMock.expectOne((r) => r.url === `${apiUrl}/threads/t1/comments`);
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, message: 'ok', data: { content: [{ id: 'c1' }], last: true } });
  });

  it('should edit a comment', () => {
    service.editComment('c1', { content: 'Edited' }).subscribe((res) => {
      expect(res.data.content).toBe('Edited');
    });
    const req = httpMock.expectOne(`${apiUrl}/comments/c1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, message: 'ok', data: { id: 'c1', content: 'Edited' } });
  });

  it('should delete a comment', () => {
    service.deleteComment('c1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/comments/c1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should report a comment', () => {
    service.reportComment('c1', { reason: 'Inappropriate' }).subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/comments/c1/report`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should mute a comment', () => {
    service.muteComment('c1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/comments/c1/mute`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should unmute a comment', () => {
    service.unmuteComment('c1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/comments/c1/mute`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, message: 'ok', data: null });
  });
});
