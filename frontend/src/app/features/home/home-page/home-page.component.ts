import { Component, OnInit, inject } from '@angular/core';
import { UserService } from '../../../core/services/UserService/user.service';

@Component({
  selector: 'app-home-page',
  standalone: false,
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css',
})
export class HomePageComponent implements OnInit {
  welcomeModal = false;

  private userService = inject(UserService);

  ngOnInit(): void {
    this.userService.getFirstLogin().subscribe((firstLogin) => {
      if (firstLogin) {
        this.welcomeModal = true;
      }
    });
  }

  closeModal() {
    this.welcomeModal = false;
    this.userService.updateFirstLogin().subscribe();
  }
}
