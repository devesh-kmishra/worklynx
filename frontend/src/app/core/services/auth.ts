import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private router = inject(Router);

  private apiUrl = `${environment.apiUrl}/auth`;

  private currentUserSignal = signal<User | null>(null);

  sessionExpired = signal(false);

  currentUser = computed(() => this.currentUserSignal());

  isAuthenticated = computed(() => !!this.currentUserSignal());

  constructor(private http: HttpClient) {}

  register(data: { name: string; email: string; password: string }): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/register`, data, {
      withCredentials: true,
    });
  }

  login(data: { email: string; password: string }): Observable<unknown> {
    return this.http.post<void>(`${this.apiUrl}/login`, data, {
      withCredentials: true,
    });
  }

  loginWithGoogle(): void {
    // Redirect to Spring Security, which will redirect to the provider
    window.location.assign(`${environment.apiUrl}/oauth2/authorization/google`);
  }

  loginWithGithub(): void {
    window.location.assign(`${environment.apiUrl}/oauth2/authorization/github`);
  }

  logout(): Observable<void> {
    return this.http
      .post<void>(
        `${this.apiUrl}/logout`,
        {},
        {
          withCredentials: true,
        },
      )
      .pipe(
        tap({
          next: () => {
            this.currentUserSignal.set(null);
            this.router.navigate(['/login']);
          },
        }),
      );
  }

  me(): Observable<User> {
    return this.http
      .get<User>(`${this.apiUrl}/me`, {
        withCredentials: true,
      })
      .pipe(
        tap({
          next: (user) => this.currentUserSignal.set(user),
        }),
      );
  }

  refresh(): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/refresh`,
      {},
      {
        withCredentials: true,
      },
    );
  }

  getConnections(): Observable<AccountConnectionsResponse> {
    return this.http.get<AccountConnectionsResponse>(`${this.apiUrl}/connections`, {
      withCredentials: true,
    });
  }

  startOAuthLink(provider: 'google' | 'github'): Observable<{ authorizationPath: string }> {
    return this.http.post<{ authorizationPath: string }>(
      `${this.apiUrl}/link/${provider}`,
      {},
      { withCredentials: true },
    );
  }

  disconnectProvider(provider: 'google' | 'github'): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/connections/${provider}`, {
      withCredentials: true,
    });
  }

  setUser(user: User | null) {
    this.currentUserSignal.set(user);
  }
}

export interface ConnectedProvider {
  provider: 'GOOGLE' | 'GITHUB';
  canDisconnect: boolean;
}

export interface AccountConnectionsResponse {
  passwordLoginEnabled: boolean;
  providers: ConnectedProvider[];
}
