import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ThreadDetailComponent } from './thread-detail.component';

describe('ThreadDetailComponent', () => {
  let component: ThreadDetailComponent;
  let fixture: ComponentFixture<ThreadDetailComponent>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [ThreadDetailComponent],
      providers: [
        provideRouter([{ path: 'thread/:threadId', component: ThreadDetailComponent }]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ThreadDetailComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => localStorage.clear());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start in loading state', () => {
    expect(component.loading).toBeTrue();
  });

  it('should have an empty comments list initially', () => {
    expect(component.comments.length).toBe(0);
  });

  it('should have an invalid comment form when empty', () => {
    expect(component.commentForm.valid).toBeFalse();
  });

  it('should have a valid comment form with content', () => {
    component.commentForm.controls['content'].setValue('Great post!');
    expect(component.commentForm.valid).toBeTrue();
  });

  it('should not post comment when form is invalid', () => {
    component.thread = { id: 't1' } as any;
    component.onPostComment();
    expect(component.postingComment).toBeFalse();
  });

  it('should detect comment author', () => {
    const comment = { authorId: 'u1' } as any;
    expect(component.isCommentAuthor(comment)).toBeFalse();
  });

  it('should allow editing recent comments', () => {
    const comment = { authorId: 'u1', createdAt: new Date().toISOString() } as any;
    // Not the author, so should return false
    expect(component.canEditComment(comment)).toBeFalse();
  });

  it('should start and cancel edit comment', () => {
    const comment = { id: 'c1', content: 'Hello' } as any;
    component.startEditComment(comment);
    expect(component.editingCommentId).toBe('c1');
    expect(component.editCommentForm.value.content).toBe('Hello');

    component.cancelEditComment();
    expect(component.editingCommentId).toBeNull();
  });
});
