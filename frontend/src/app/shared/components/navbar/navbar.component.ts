import { Component, inject } from '@angular/core';
import { AuthServiceService } from '../../../core/services/AuthService/auth-service.service';
import { Observable } from 'rxjs';
import { User } from '../../models/user';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  private authService = inject(AuthServiceService);

  user$: Observable<User | null> = this.authService.user$;

  logout() {
    this.authService.logout();
  }
}
