import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private fb = inject(FormBuilder);

  private authService = inject(AuthService);

  private router = inject(Router);

  loading = signal(false);

  error = signal('');

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);

    this.error.set('');

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.authService.me().subscribe({
          next: () => {
            this.router.navigate(['/tasks']);
          },
        });
      },

      error: (err) => {
        this.loading.set(false);

        this.error.set(err?.error?.message ?? 'Login failed. Please try again.');
      },
    });
  }
}
