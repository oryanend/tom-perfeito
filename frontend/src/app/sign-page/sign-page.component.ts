import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthServiceService} from "../services/auth-service.service";
import {Router} from '@angular/router';

@Component({
  selector: 'app-sign-page',
  standalone: false,
  templateUrl: './sign-page.component.html',
  styleUrl: './sign-page.component.css'
})
export class SignPageComponent {
  signinForm: FormGroup;

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
      next: (response) => {
        console.log('Registration successful:', response);
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Registration failed:', error);
      }
    })

    console.log(this.signinForm.value);
  }
}
