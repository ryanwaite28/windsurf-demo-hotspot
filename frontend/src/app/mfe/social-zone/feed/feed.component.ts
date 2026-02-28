import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeedService } from '../../../shared/services/feed.service';
import { Thread } from '../../../shared/models/social.models';
import { ThreadCardComponent } from '../thread-card/thread-card.component';
import { ComposeThreadComponent } from '../compose-thread/compose-thread.component';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, ThreadCardComponent, ComposeThreadComponent],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss',
})
export class FeedComponent implements OnInit {
  threads: Thread[] = [];
  loading = true;
  error = '';
  page = 0;
  hasMore = true;
  loadingMore = false;

  constructor(private feedService: FeedService) {}

  ngOnInit(): void {
    this.loadFeed();
  }

  loadFeed(): void {
    this.loading = true;
    this.feedService.getFeed(this.page).subscribe({
      next: (res) => {
        this.threads = res.data.content;
        this.hasMore = !res.data.last;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load feed';
      },
    });
  }

  loadMore(): void {
    this.loadingMore = true;
    this.page++;
    this.feedService.getFeed(this.page).subscribe({
      next: (res) => {
        this.threads = [...this.threads, ...res.data.content];
        this.hasMore = !res.data.last;
        this.loadingMore = false;
      },
      error: () => {
        this.loadingMore = false;
        this.page--;
      },
    });
  }

  onThreadCreated(thread: Thread): void {
    this.threads = [thread, ...this.threads];
  }

  onThreadDeleted(threadId: string): void {
    this.threads = this.threads.filter((t) => t.id !== threadId);
  }
}
