import {Component, inject, OnInit} from '@angular/core';
import {AuthServiceService} from './core/services/AuthService/auth-service.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css',
})

export class AppComponent implements OnInit {
  title = 'frontend'

  private authService = inject(AuthServiceService);

  ngOnInit() {
    this.authService.restoreSession();
  }
}
