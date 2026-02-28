import { Routes } from '@angular/router';
import { authGuard } from '../../shared/guards/auth.guard';

export const SOCIAL_ZONE_ROUTES: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./feed/feed.component').then((m) => m.FeedComponent),
  },
  {
    path: 'thread/:threadId',
    loadComponent: () =>
      import('./thread-detail/thread-detail.component').then((m) => m.ThreadDetailComponent),
  },
];
