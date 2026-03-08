import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {Router} from '@angular/router';
import {AuthServiceService} from '../services/AuthService/auth-service.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-login-page',
  standalone: false,
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})

export class LoginPageComponent {
  loginForm: FormGroup;

  isLoading = false;

  alertType: 'success' | 'warning' | 'error' | null = null;
  alertMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthServiceService,
    private router: Router,
    private route: ActivatedRoute
  ) {

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    }

    this.route.queryParams.subscribe(params => {
      if (params['isRegistered']) {
        this.showAlert('success', 'Thank you for registering! Now you can log in.');
      }
    });
  }

  showAlert(type: 'success' | 'warning' | 'error', message: string) {
    this.alertType = type;
    this.alertMessage = message;
  }

  clearAlert() {
    this.alertType = null;
    this.alertMessage = '';
  }

  onSubmit() {

    this.clearAlert();

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        this.authService.saveToken(response.access_token);
        this.router.navigate(['/home']);
        this.isLoading = false;
      },
      error: (error) => {

        this.isLoading = false;

        if (error.status === 0) {
          this.showAlert('error', 'Unable to connect to the server. Please try again later.');
        }

        else if (error.status === 401) {
          this.showAlert('warning', error.error?.message || 'Invalid email or password. Please try again.');
        }

      }
    });
  }
}
