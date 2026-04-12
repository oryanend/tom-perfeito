import { Component, OnInit, inject } from '@angular/core';
import { MusicService } from '../../../../core/services/MusicService/music.service';
import { debounceTime, Subject, switchMap } from 'rxjs';
import { Router } from '@angular/router';
import { MusicSearchItem } from '../../../models/music-search-item';

@Component({
  selector: 'app-search-bar',
  standalone: false,
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
})
export class SearchBarComponent implements OnInit {
  private musicService = inject(MusicService);
  private router = inject(Router);

  search$ = new Subject<string>();
  showSuggestions = false;
  searchTerm = '';
  musics: MusicSearchItem[] = [];

  ngOnInit() {
    this.search$
      .pipe(
        debounceTime(300),
        switchMap((value) => this.musicService.searchMusicByName(value))
      )
      .subscribe((response) => {
        this.musics = response.content;
      });
  }

  onSearch() {
    if (!this.searchTerm.trim()) {
      this.musics = [];
      return;
    }

    this.search$.next(this.searchTerm);
  }

  hideSuggestions() {
    setTimeout(() => {
      this.showSuggestions = false;
    }, 200);
  }

  goToMusic(music: MusicSearchItem) {
    this.router.navigate(['/music', music.id]);
  }
}
