import { Routes } from '@angular/router';
import { authGuard } from '../../shared/guards/auth.guard';

export const USER_SETTINGS_ROUTES: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./settings-profile/settings-profile.component').then((m) => m.SettingsProfileComponent),
  },
  {
    path: 'privacy',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./settings-privacy/settings-privacy.component').then((m) => m.SettingsPrivacyComponent),
  },
  {
    path: 'security',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./settings-security/settings-security.component').then((m) => m.SettingsSecurityComponent),
  },
];
