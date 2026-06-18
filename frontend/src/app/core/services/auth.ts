import { computed, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  private currentUserSignal = signal<User | null>(null);

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
          next: () => this.currentUserSignal.set(null),
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

  setUser(user: User | null) {
    this.currentUserSignal.set(user);
  }
}
