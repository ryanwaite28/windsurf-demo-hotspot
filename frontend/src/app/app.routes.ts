import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./mfe/user-entry/user-entry.routes').then((m) => m.USER_ENTRY_ROUTES),
  },
  {
    path: 'profile',
    loadChildren: () =>
      import('./mfe/user-profile/user-profile.routes').then((m) => m.USER_PROFILE_ROUTES),
  },
  {
    path: 'settings',
    loadChildren: () =>
      import('./mfe/user-settings/user-settings.routes').then((m) => m.USER_SETTINGS_ROUTES),
  },
  {
    path: 'zones/social',
    loadChildren: () =>
      import('./mfe/social-zone/social-zone.routes').then((m) => m.SOCIAL_ZONE_ROUTES),
  },
  {
    path: '**',
    redirectTo: '',
  },
];
