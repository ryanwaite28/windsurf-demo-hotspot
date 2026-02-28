import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ThreadCardComponent } from './thread-card.component';
import { Thread } from '../../../shared/models/social.models';

describe('ThreadCardComponent', () => {
  let component: ThreadCardComponent;
  let fixture: ComponentFixture<ThreadCardComponent>;

  const mockThread: Thread = {
    id: 't1',
    authorId: 'u1',
    authorUsername: 'testuser',
    authorDisplayName: 'Test User',
    authorProfilePictureUrl: null,
    content: 'Hello world',
    threadType: 'ORIGINAL',
    parentThreadId: null,
    parentThread: null,
    isPinned: false,
    media: [],
    poll: null,
    likesCount: 5,
    commentsCount: 3,
    repostsCount: 1,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [ThreadCardComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(ThreadCardComponent);
    component = fixture.componentInstance;
    component.thread = { ...mockThread };
    fixture.detectChanges();
  });

  afterEach(() => localStorage.clear());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display author name', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.author-name')?.textContent).toContain('Test User');
  });

  it('should display thread content', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.thread-content')?.textContent).toContain('Hello world');
  });

  it('should have correct like count on thread input', () => {
    expect(component.thread.likesCount).toBe(5);
  });

  it('should have correct comment count on thread input', () => {
    expect(component.thread.commentsCount).toBe(3);
  });

  it('should show avatar placeholder when no picture', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.thread-avatar-placeholder')?.textContent?.trim()).toBe('T');
  });

  it('should show repost badge for reposts', () => {
    component.thread = { ...mockThread, threadType: 'REPOST' };
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.badge-repost')).toBeTruthy();
  });

  it('should show quote badge for quotes', () => {
    component.thread = { ...mockThread, threadType: 'QUOTE' };
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.badge-quote')).toBeTruthy();
  });

  it('should compute timeAgo as just now for recent threads', () => {
    expect(component.timeAgo).toBe('just now');
  });

  it('should toggle menu visibility', () => {
    expect(component.showMenu).toBeFalse();
    component.toggleMenu();
    expect(component.showMenu).toBeTrue();
    component.toggleMenu();
    expect(component.showMenu).toBeFalse();
  });
});
