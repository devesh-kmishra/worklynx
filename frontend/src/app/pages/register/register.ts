import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private fb = inject(FormBuilder);

  private authService = inject(AuthService);

  private router = inject(Router);

  loading = signal(false);

  error = signal('');

  registerForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);

    this.error.set('');

    this.authService.register(this.registerForm.getRawValue()).subscribe({
      next: () => {
        this.authService.me().subscribe({
          next: () => {
            this.router.navigate(['/dashboard']);
          },
        });
      },
      error: (err) => {
        this.loading.set(false);

        this.error.set(err?.error?.message ?? 'Registration failed. Please try again.');
      },
    });
  }
}
