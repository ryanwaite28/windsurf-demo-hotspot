import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ThreadService } from './thread.service';
import { environment } from '../../../environments/environment';

describe('ThreadService', () => {
  let service: ThreadService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiBaseUrl}/social`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ThreadService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ThreadService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a thread', () => {
    service.createThread({ content: 'Hello world' }).subscribe((res) => {
      expect(res.data.content).toBe('Hello world');
    });
    const req = httpMock.expectOne(`${apiUrl}/threads`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.content).toBe('Hello world');
    req.flush({ success: true, message: 'ok', data: { id: '1', content: 'Hello world' } });
  });

  it('should get a thread by id', () => {
    service.getThread('t1').subscribe((res) => {
      expect(res.data.id).toBe('t1');
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1`);
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, message: 'ok', data: { id: 't1' } });
  });

  it('should edit a thread', () => {
    service.editThread('t1', { content: 'Edited' }).subscribe((res) => {
      expect(res.data.content).toBe('Edited');
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, message: 'ok', data: { id: 't1', content: 'Edited' } });
  });

  it('should delete a thread', () => {
    service.deleteThread('t1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should repost a thread', () => {
    service.repostThread('t1').subscribe((res) => {
      expect(res.data.threadType).toBe('REPOST');
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/repost`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: { id: 't2', threadType: 'REPOST' } });
  });

  it('should quote a thread', () => {
    service.quoteThread('t1', { content: 'My take' }).subscribe((res) => {
      expect(res.data.threadType).toBe('QUOTE');
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/quote`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: { id: 't3', threadType: 'QUOTE' } });
  });

  it('should like a thread', () => {
    service.likeThread('t1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/like`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should unlike a thread', () => {
    service.unlikeThread('t1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/like`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should save a thread', () => {
    service.saveThread('t1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/save`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should unsave a thread', () => {
    service.unsaveThread('t1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/save`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should report a thread', () => {
    service.reportThread('t1', { reason: 'Spam' }).subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/report`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.reason).toBe('Spam');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should mute a thread', () => {
    service.muteThread('t1').subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/mute`);
    expect(req.request.method).toBe('POST');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should toggle pin', () => {
    service.togglePin('t1').subscribe((res) => {
      expect(res.data.isPinned).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/pin`);
    expect(req.request.method).toBe('PUT');
    req.flush({ success: true, message: 'ok', data: { id: 't1', isPinned: true } });
  });

  it('should react to a thread', () => {
    service.reactToThread('t1', { reactionType: 'FIRE' }).subscribe((res) => {
      expect(res.success).toBeTrue();
    });
    const req = httpMock.expectOne(`${apiUrl}/threads/t1/react`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body.reactionType).toBe('FIRE');
    req.flush({ success: true, message: 'ok', data: null });
  });

  it('should get user threads', () => {
    service.getUserThreads('u1').subscribe((res) => {
      expect(res.data.content.length).toBe(1);
    });
    const req = httpMock.expectOne((r) => r.url === `${apiUrl}/users/u1/threads`);
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, message: 'ok', data: { content: [{ id: 't1' }], last: true } });
  });
});
