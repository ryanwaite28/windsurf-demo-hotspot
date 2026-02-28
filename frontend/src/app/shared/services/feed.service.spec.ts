import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { FeedService } from './feed.service';
import { environment } from '../../../environments/environment';

describe('FeedService', () => {
  let service: FeedService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiBaseUrl}/social`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FeedService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(FeedService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get the feed', () => {
    service.getFeed().subscribe((res) => {
      expect(res.data.content.length).toBe(2);
    });
    const req = httpMock.expectOne((r) => r.url === `${apiUrl}/feed`);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('0');
    req.flush({
      success: true, message: 'ok',
      data: { content: [{ id: 't1' }, { id: 't2' }], last: false, totalElements: 10, totalPages: 5, size: 20, number: 0, first: true },
    });
  });

  it('should pass page parameter', () => {
    service.getFeed(2, 10).subscribe();
    const req = httpMock.expectOne((r) => r.url === `${apiUrl}/feed`);
    expect(req.request.params.get('page')).toBe('2');
    expect(req.request.params.get('size')).toBe('10');
    req.flush({ success: true, message: 'ok', data: { content: [], last: true } });
  });
});
