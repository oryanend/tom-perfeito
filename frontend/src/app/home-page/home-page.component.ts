import {Component, OnInit} from '@angular/core';
import {UserService} from '../services/UserService/user.service';

@Component({
  selector: 'app-home-page',
  standalone: false,
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit {
  welcomeModal = false;

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    this.userService.getFirstLogin().subscribe(firstLogin => {
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
