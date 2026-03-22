import { Component, OnInit } from '@angular/core';
import { MusicService } from '../../../../core/services/MusicService/music.service';
import { debounceTime, Subject, switchMap } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  standalone: false,
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
})
export class SearchBarComponent implements OnInit {
  searchTerm: string = '';
  showSuggestions = false;

  musics: any[] = [];
  search$ = new Subject<string>();

  constructor(private musicService: MusicService) {}

  ngOnInit() {
    this.search$
      .pipe(
        debounceTime(300),
        switchMap((value) => this.musicService.searchMusicByName(value))
      )
      .subscribe((response) => {
        this.musics = response.content; // Page<MusicMinDTO>
      });
  }

  onSearch() {
    if (!this.searchTerm.trim()) {
      this.musics = [];
      return;
    }

    this.search$.next(this.searchTerm);
  }

  selectItem(music: any) {
    this.searchTerm = music.name;
    this.showSuggestions = false;
  }

  hideSuggestions() {
    setTimeout(() => {
      this.showSuggestions = false;
    }, 200);
  }
}
