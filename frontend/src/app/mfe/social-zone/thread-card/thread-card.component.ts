import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Thread } from '../../../shared/models/social.models';
import { AuthService } from '../../../shared/services/auth.service';
import { ThreadService } from '../../../shared/services/thread.service';

@Component({
  selector: 'app-thread-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './thread-card.component.html',
  styleUrl: './thread-card.component.scss',
})
export class ThreadCardComponent {
  @Input() thread!: Thread;
  @Input() showActions = true;
  @Output() deleted = new EventEmitter<string>();

  liked = false;
  saved = false;
  showMenu = false;

  constructor(
    public authService: AuthService,
    private threadService: ThreadService,
  ) {}

  get isAuthor(): boolean {
    return this.authService.currentUser?.id === this.thread.authorId;
  }

  get timeAgo(): string {
    const now = new Date();
    const created = new Date(this.thread.createdAt);
    const diffMs = now.getTime() - created.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    if (diffMins < 1) return 'just now';
    if (diffMins < 60) return `${diffMins}m`;
    const diffHours = Math.floor(diffMins / 60);
    if (diffHours < 24) return `${diffHours}h`;
    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays}d`;
  }

  onLike(): void {
    if (this.liked) {
      this.threadService.unlikeThread(this.thread.id).subscribe(() => {
        this.liked = false;
        this.thread.likesCount--;
      });
    } else {
      this.threadService.likeThread(this.thread.id).subscribe(() => {
        this.liked = true;
        this.thread.likesCount++;
      });
    }
  }

  onSave(): void {
    if (this.saved) {
      this.threadService.unsaveThread(this.thread.id).subscribe(() => {
        this.saved = false;
      });
    } else {
      this.threadService.saveThread(this.thread.id).subscribe(() => {
        this.saved = true;
      });
    }
  }

  onRepost(): void {
    this.threadService.repostThread(this.thread.id).subscribe(() => {
      this.thread.repostsCount++;
    });
  }

  onDelete(): void {
    if (confirm('Delete this thread?')) {
      this.threadService.deleteThread(this.thread.id).subscribe(() => {
        this.deleted.emit(this.thread.id);
      });
    }
  }

  onTogglePin(): void {
    this.threadService.togglePin(this.thread.id).subscribe((res) => {
      this.thread.isPinned = res.data.isPinned;
    });
  }

  toggleMenu(): void {
    this.showMenu = !this.showMenu;
  }
}
