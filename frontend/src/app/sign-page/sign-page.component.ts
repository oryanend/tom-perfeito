import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-sign-page',
  standalone: false,
  templateUrl: './sign-page.component.html',
  styleUrl: './sign-page.component.css'
})
export class SignPageComponent {
  signinForm: FormGroup;
  constructor(private fb: FormBuilder) {
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

    console.log(this.signinForm.value);
  }
}
