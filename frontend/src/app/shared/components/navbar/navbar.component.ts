import { Component, inject } from '@angular/core';
import { AuthServiceService } from '../../../core/services/AuthService/auth-service.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  user$!: Observable<string | null>;

  // use inject() to satisfy lint rule and remove constructor
  private authService = inject(AuthServiceService);

  constructor() {
    this.user$ = this.authService.user$;
  }

  logout() {
    this.authService.logout();
  }
}
