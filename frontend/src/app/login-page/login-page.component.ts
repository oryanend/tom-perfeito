import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {Router} from '@angular/router';
import {AuthServiceService} from '../services/AuthService/auth-service.service';

@Component({
  selector: 'app-login-page',
  standalone: false,
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})

export class LoginPageComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthServiceService, private router: Router) {
    this.loginForm = this.fb.group({

      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    })
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        console.log('Registration successful:', response);
        this.authService.saveToken(response.access_token);
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Registration failed:', error);
      }
    })

    console.log(this.loginForm.value);
  }
}
