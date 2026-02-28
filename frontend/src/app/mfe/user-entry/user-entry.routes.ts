import { Routes } from '@angular/router';
import { guestGuard } from '../../shared/guards/auth.guard';

export const USER_ENTRY_ROUTES: Routes = [
  {
    path: '',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'signup',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./signup/signup.component').then((m) => m.SignupComponent),
  },
];
