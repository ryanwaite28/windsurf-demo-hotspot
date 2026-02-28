import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ThreadService } from '../../../shared/services/thread.service';
import { CommentService } from '../../../shared/services/comment.service';
import { AuthService } from '../../../shared/services/auth.service';
import { Thread, ThreadComment } from '../../../shared/models/social.models';
import { ThreadCardComponent } from '../thread-card/thread-card.component';

@Component({
  selector: 'app-thread-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ThreadCardComponent],
  templateUrl: './thread-detail.component.html',
  styleUrl: './thread-detail.component.scss',
})
export class ThreadDetailComponent implements OnInit {
  thread: Thread | null = null;
  comments: ThreadComment[] = [];
  loading = true;
  error = '';
  commentForm: FormGroup;
  postingComment = false;
  commentError = '';
  commentsPage = 0;
  hasMoreComments = true;
  loadingMoreComments = false;
  editingCommentId: string | null = null;
  editCommentForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private threadService: ThreadService,
    private commentService: CommentService,
    public authService: AuthService,
    private fb: FormBuilder,
  ) {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(500)]],
    });
    this.editCommentForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(500)]],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.loadThread(params['threadId']);
    });
  }

  loadThread(threadId: string): void {
    this.loading = true;
    this.threadService.getThread(threadId).subscribe({
      next: (res) => {
        this.thread = res.data;
        this.loading = false;
        this.loadComments(threadId);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.status === 404 ? 'Thread not found' : 'Failed to load thread';
      },
    });
  }

  loadComments(threadId: string): void {
    this.commentService.getComments(threadId, this.commentsPage).subscribe({
      next: (res) => {
        this.comments = res.data.content;
        this.hasMoreComments = !res.data.last;
      },
    });
  }

  loadMoreComments(): void {
    if (!this.thread) return;
    this.loadingMoreComments = true;
    this.commentsPage++;
    this.commentService.getComments(this.thread.id, this.commentsPage).subscribe({
      next: (res) => {
        this.comments = [...this.comments, ...res.data.content];
        this.hasMoreComments = !res.data.last;
        this.loadingMoreComments = false;
      },
      error: () => {
        this.loadingMoreComments = false;
        this.commentsPage--;
      },
    });
  }

  onPostComment(): void {
    if (this.commentForm.invalid || !this.thread) return;

    this.postingComment = true;
    this.commentError = '';

    this.commentService.createComment(this.thread.id, this.commentForm.value).subscribe({
      next: (res) => {
        this.postingComment = false;
        this.commentForm.reset();
        this.comments = [res.data, ...this.comments];
        this.thread!.commentsCount++;
      },
      error: (err) => {
        this.postingComment = false;
        this.commentError = err.error?.message || 'Failed to post comment';
      },
    });
  }

  startEditComment(comment: ThreadComment): void {
    this.editingCommentId = comment.id;
    this.editCommentForm.setValue({ content: comment.content });
  }

  cancelEditComment(): void {
    this.editingCommentId = null;
    this.editCommentForm.reset();
  }

  onSaveEditComment(commentId: string): void {
    if (this.editCommentForm.invalid) return;

    this.commentService.editComment(commentId, this.editCommentForm.value).subscribe({
      next: (res) => {
        const idx = this.comments.findIndex((c) => c.id === commentId);
        if (idx !== -1) {
          this.comments[idx] = res.data;
        }
        this.editingCommentId = null;
        this.editCommentForm.reset();
      },
    });
  }

  onDeleteComment(commentId: string): void {
    if (!confirm('Delete this comment?')) return;

    this.commentService.deleteComment(commentId).subscribe({
      next: () => {
        this.comments = this.comments.filter((c) => c.id !== commentId);
        if (this.thread) this.thread.commentsCount--;
      },
    });
  }

  isCommentAuthor(comment: ThreadComment): boolean {
    return this.authService.currentUser?.id === comment.authorId;
  }

  canEditComment(comment: ThreadComment): boolean {
    if (!this.isCommentAuthor(comment)) return false;
    const created = new Date(comment.createdAt);
    const now = new Date();
    return (now.getTime() - created.getTime()) < 600000; // 10 minutes
  }

  onThreadDeleted(): void {
    window.history.back();
  }
}
