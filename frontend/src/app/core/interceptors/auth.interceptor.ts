import { HttpContextToken, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, finalize, Observable, shareReplay, switchMap, tap, throwError } from 'rxjs';
import { AuthService } from '../services/auth';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

const AUTH_RETRIED = new HttpContextToken<boolean>(() => false);

// Shared by all requests using this interceptor.
let refreshInProgress: Observable<void> | null = null;

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isApiRequest = request.url.startsWith(environment.apiUrl);

  // Leave requests to other origins untouched.
  const apiRequest = isApiRequest ? request.clone({ withCredentials: true }) : request;

  const url = request.url.split('?')[0];

  const excludedFromRefresh = [
    `${environment.apiUrl}/auth/login`,
    `${environment.apiUrl}/auth/register`,
    `${environment.apiUrl}/auth/refresh`,
    `${environment.apiUrl}/auth/logout`,
  ].includes(url);

  return next(apiRequest).pipe(
    catchError((error) => {
      const shouldRefresh =
        isApiRequest &&
        error.status === 401 &&
        !excludedFromRefresh &&
        !request.context.get(AUTH_RETRIED);

      if (!shouldRefresh) {
        authService.sessionExpired.set(true);
        return throwError(() => error);
      }

      // Create one refresh operation shared by concurrent requests.
      if (!refreshInProgress) {
        refreshInProgress = authService.refresh().pipe(
          tap({
            error: () => authService.setUser(null),
          }),
          catchError((refreshError) => throwError(() => refreshError)),
          finalize(() => {
            refreshInProgress = null;
          }),
          shareReplay({ bufferSize: 1, refCount: false }),
        );
      }

      return refreshInProgress.pipe(
        switchMap(() => {
          // Retry this original request at most once.
          const retryRequest = apiRequest.clone({
            context: apiRequest.context.set(AUTH_RETRIED, true),
          });

          return next(retryRequest);
        }),
        catchError((refreshError) => {
          authService.setUser(null);

          if (router.url !== '/login' && router.url !== '/register') {
            void router.navigate(['/login'], {
              queryParams: { sessionExpired: true },
            });
          }

          return throwError(() => refreshError);
        }),
      );
    }),
  );
};
