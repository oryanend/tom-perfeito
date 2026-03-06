import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthServiceService} from "../services/AuthService/auth-service.service";
import {Router} from '@angular/router';

@Component({
  selector: 'app-sign-page',
  standalone: false,
  templateUrl: './sign-page.component.html',
  styleUrl: './sign-page.component.css'
})
export class SignPageComponent {
  signinForm: FormGroup;
  errorMsg: string | null = null;
  warningMsg: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthServiceService, private router: Router) {
    this.signinForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.signinForm.invalid) {
      this.signinForm.markAllAsTouched();
      return;
    }

    this.authService.register(this.signinForm.value).subscribe({
      next: () => {
        this.router.navigate(['/login'], { queryParams: { registered: 'true' } });
      },
      error: (error) => {
        if (error.status === 0){
          this.errorMsg = 'Unable to connect to the server. Please try again later.';
        } else if (error.status === 400) {
          this.warningMsg = error.error?.message || 'Email or username already exists. Please choose a different one.';
        }
      },
    });
  }
}
