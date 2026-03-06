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
  registered = false;
  errorMsg: string | null = null;
  warningMsg: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthServiceService, private router: Router, private route:ActivatedRoute) {
    this.loginForm = this.fb.group({

      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    })
  }

  ngOnInit():void {
    this.route.queryParams.subscribe(params => {
      if (params['registered']) {
        this.registered = true;
      }
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        this.authService.saveToken(response.access_token);
        this.router.navigate(['/home']);
      },
      error: (error) => {
        if (error.status === 0){
          this.errorMsg = 'Unable to connect to the server. Please try again later.';
        } else if (error.status === 401) {
          this.warningMsg = error.error?.message || 'Invalid email or password. Please try again.';
        }
      }
    });
  }
}
