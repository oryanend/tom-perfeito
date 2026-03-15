import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {UserService} from '../../../core/services/UserService/user.service';

@Component({
  selector: 'app-welcome-modal',
  standalone: false,
  templateUrl: './welcome-modal.component.html',
  styleUrl: './welcome-modal.component.css'
})
export class WelcomeModalComponent {
  @Input() isOpen = false;
  @Output() closed = new EventEmitter<void>();

  private userService = inject(UserService);

  closeModal() {
    this.isOpen = false;

    this.userService.updateFirstLogin().subscribe();

    this.closed.emit();
  }
}
