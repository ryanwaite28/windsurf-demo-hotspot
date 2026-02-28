import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { FeedComponent } from './feed.component';
import { environment } from '../../../../environments/environment';

describe('FeedComponent', () => {
  let component: FeedComponent;
  let fixture: ComponentFixture<FeedComponent>;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiBaseUrl}/social`;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeedComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(FeedComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start in loading state', () => {
    expect(component.loading).toBeTrue();
  });

  it('should load feed on init', () => {
    fixture.detectChanges();
    const req = httpMock.expectOne((r) => r.url === `${apiUrl}/feed`);
    req.flush({
      success: true, message: 'ok',
      data: { content: [{ id: 't1', content: 'Hello', authorDisplayName: 'Test', authorUsername: 'test', threadType: 'ORIGINAL', media: [], likesCount: 0, commentsCount: 0, repostsCount: 0 }], last: true },
    });
    expect(component.threads.length).toBe(1);
    expect(component.loading).toBeFalse();
    expect(component.hasMore).toBeFalse();
  });

  it('should prepend new thread on creation', () => {
    fixture.detectChanges();
    httpMock.expectOne((r) => r.url === `${apiUrl}/feed`).flush({
      success: true, message: 'ok', data: { content: [], last: true },
    });

    const newThread = { id: 'new', content: 'New thread' } as any;
    component.onThreadCreated(newThread);
    expect(component.threads[0].id).toBe('new');
  });

  it('should remove thread on delete', () => {
    component.threads = [{ id: 't1' } as any, { id: 't2' } as any];
    component.onThreadDeleted('t1');
    expect(component.threads.length).toBe(1);
    expect(component.threads[0].id).toBe('t2');
  });
});
