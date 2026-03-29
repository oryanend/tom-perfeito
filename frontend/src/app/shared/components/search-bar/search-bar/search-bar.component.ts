import { Component, OnInit, inject } from '@angular/core';
import { MusicService } from '../../../../core/services/MusicService/music.service';
import { debounceTime, Subject, switchMap } from 'rxjs';
import { Router } from '@angular/router';

// 🔥 Crie um tipo para o autocomplete (mínimo necessário)
interface MusicSearchItem {
  id: string;
  title: string;
}

@Component({
  selector: 'app-search-bar',
  standalone: false,
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
})
export class SearchBarComponent implements OnInit {
  private musicService = inject(MusicService);
  private router = inject(Router);

  searchTerm = '';
  showSuggestions = false;

  musics: MusicSearchItem[] = [];
  search$ = new Subject<string>();

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

  selectItem(music: MusicSearchItem) {
    this.searchTerm = music.title;
    this.showSuggestions = false;
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
