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
  isRegistered = false;
  isLoading = false;
  errorMsg: string | null = null;
  warningMsg: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthServiceService, private router: Router, private route:ActivatedRoute) {
    this.loginForm = this.fb.group({

      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    })

    // if already authenticated, redirect to home immediately
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/home']);
    }

    // subscribe to query params here instead of ngOnInit to avoid unused lifecycle warning
    this.route.queryParams.subscribe(params => {
      if (params['isRegistered']) {
        this.isRegistered = true;
      }
    });
  }

  onSubmit() {
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

        if (error.status === 0){
          this.errorMsg = 'Unable to connect to the server. Please try again later.';
        } else if (error.status === 401) {
          this.warningMsg = error.error?.message || 'Invalid email or password. Please try again.';
        }
      }
    });
  }
}
