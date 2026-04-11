import { Component, inject, OnInit } from '@angular/core';
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

  getTimeAgo(dateString: string): string {
    const now = new Date();
    const past = new Date(dateString);

    const diffMs = now.getTime() - past.getTime();

    const seconds = Math.floor(diffMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (seconds < 60) return 'Just right now';
    if (minutes < 60) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
    if (hours < 24) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
    return `${days} day${days > 1 ? 's' : ''} ago`;
  }
}
