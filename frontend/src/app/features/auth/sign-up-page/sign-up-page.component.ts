import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthServiceService } from '../../../core/services/AuthService/auth-service.service';
import { Router } from '@angular/router';
import { ApiError } from '../../../core/errors/api/api-errors';
import { NetworkError } from '../../../core/errors/network/network-error';

@Component({
  selector: 'app-sign-page',
  standalone: false,
  templateUrl: './sign-up-page.component.html',
  styleUrl: './sign-up-page.component.css',
})
export class SignUpPageComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthServiceService);
  private router = inject(Router);

  signinForm: FormGroup = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  isLoading = false;

  alertType: 'warning' | 'error' | null = null;
  alertMessage = '';

  constructor() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    }
  }

  showAlert(type: 'warning' | 'error', message: string) {
    this.alertType = type;
    this.alertMessage = message;
  }

  clearAlert() {
    this.alertType = null;
    this.alertMessage = '';
  }

  onSubmit() {
    this.clearAlert();

    if (this.signinForm.invalid) {
      this.signinForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    this.authService.register(this.signinForm.value).subscribe({
      next: () => {
        this.router.navigate(['/login'], { queryParams: { isRegistered: 'true' } });
        this.isLoading = false;
      },

      error: (error) => {
        this.isLoading = false;

        if (error instanceof NetworkError) {
          this.showAlert('error', 'Unable to connect to the server. Please try again later.');
        }

        if (error instanceof ApiError) {
          this.showAlert('warning', error.message || 'Email or username already exists.');
        }
      },
    });
  }
}
