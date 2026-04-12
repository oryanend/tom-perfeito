import { Component, ElementRef, inject, ViewChild } from '@angular/core';
import { Modal } from 'bootstrap';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-modal',
  standalone: false,
  templateUrl: './login-modal.component.html',
  styleUrl: './login-modal.component.css',
})
export class LoginModalComponent {
  @ViewChild('modalRef') modalElement!: ElementRef;
  private modalInstance!: Modal;
  private router = inject(Router);

  openLoginModal() {
    const element = document.getElementById('loginModal');
    if (!element) return;

    this.modalInstance = new Modal(element);
    this.modalInstance.show();
  }

  close() {
    this.modalInstance.hide();
  }

  goToLogin() {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }

    this.router.navigate(['/login']);
  }
}
