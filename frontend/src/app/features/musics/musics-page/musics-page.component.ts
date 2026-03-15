import { Component, OnInit, inject } from '@angular/core';
import { MusicService } from '../../../core/services/MusicService/music.service';
import { Music } from '../../../shared/models/music';

@Component({
  selector: 'app-musics-page',
  standalone: false,
  templateUrl: './musics-page.component.html',
  styleUrl: './musics-page.component.css',
})
export class MusicsPageComponent implements OnInit {
  musics: Music[] = [];

  currentPage = 0;
  totalPages = 0;
  pages: number[] = [];

  private musicService = inject(MusicService);

  ngOnInit(): void {
    this.loadMusics();
  }

  loadMusics(page = 0) {
    this.musicService.getAllMusics(page).subscribe((response) => {
      this.musics = response.content;

      this.currentPage = response.number;
      this.totalPages = response.totalPages;

      this.pages = Array(this.totalPages)
        .fill(0)
        .map((x, i) => i);
    });
  }

  goToPage(page: number) {
    this.loadMusics(page);
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.loadMusics(this.currentPage + 1);
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.loadMusics(this.currentPage - 1);
    }
  }
}
