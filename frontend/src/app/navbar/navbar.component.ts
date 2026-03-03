import { Component } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import {AuthServiceService} from '../services/AuthService/auth-service.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  user$!: Observable<string | null>;

  constructor(private authService: AuthServiceService) {
    this.user$ = this.authService.user$;
  }

  logout() {
    this.authService.logout();
  }
}
