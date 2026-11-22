import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated() || router.url === '/login' || router.url === '/register') {
    return true;
  }

  return router.createUrlTree(['/login'], {
    queryParams: authService.sessionExpired() ? { sessionExpired: true } : {},
  });
};
