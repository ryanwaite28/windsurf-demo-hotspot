import { Routes } from '@angular/router';

export const USER_PROFILE_ROUTES: Routes = [
  {
    path: ':username',
    loadComponent: () =>
      import('./profile-view/profile-view.component').then((m) => m.ProfileViewComponent),
  },
];
