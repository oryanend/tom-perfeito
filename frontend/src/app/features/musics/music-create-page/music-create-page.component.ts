import { Component, inject, OnInit } from '@angular/core';
import { MusicService } from '../../../core/services/MusicService/music.service';
import { ChordService } from '../../../core/services/ChordService/chord.service';
import { LyricChord } from '../../../shared/models/lyric-chord';
import { Lyric } from '../../../shared/models/lyric';
import { Chord } from '../../../shared/models/chord';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-music-create-page',
  standalone: false,
  templateUrl: './music-create-page.component.html',
  styleUrl: './music-create-page.component.css',
})
export class MusicCreatePageComponent implements OnInit {
  private musicService = inject(MusicService);
  private chordService = inject(ChordService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  currentStep = 1;
  signinForm!: FormGroup;
  isLoading = false;

  music = {
    title: '',
    description: '',
    releaseDate: '',
    lyric: {
      text: '',
      chords: [] as LyricChord[],
    } as Lyric,
  };

  textArray: string[] = [];
  hoverIndex: number | null = null;
  selectedPosition: number | null = null;

  // POSIÇÃO DO POPUP
  popupX = 0;
  popupY = 0;

  chords: LyricChord[] = [];
  allChords: Chord[] = [];
  filteredChords: Chord[] = [];
  searchChord = '';

  ngOnInit() {
    this.loadChords();

    this.signinForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      releaseDate: ['', Validators.required],
      lyrics: ['', Validators.required],
    });
  }

  loadChords() {
    this.chordService.getAll().subscribe({
      next: (res) => {
        this.allChords = res.content;
        this.filteredChords = this.allChords;
      },
      error: (err) => console.error('Erro ao carregar acordes', err),
    });
  }

  prepareEditor() {
    this.textArray = Array.from(this.music.lyric.text);
  }

  openChordInput(index: number, event: Event) {
    const mouseEvent = event as MouseEvent;

    if (mouseEvent.clientX !== undefined) {
      this.popupX = mouseEvent.clientX;
      this.popupY = mouseEvent.clientY;
    }

    this.selectedPosition = index;
  }

  filterChords() {
    this.filteredChords = this.allChords.filter((c) =>
      c.name.toLowerCase().includes(this.searchChord.toLowerCase())
    );
  }

  selectChord(chord: Chord) {
    if (this.selectedPosition !== null) {
      this.chords = this.chords.filter((c) => c.position !== this.selectedPosition);

      this.chords.push({
        chordId: chord.id,
        position: this.selectedPosition,
      } as LyricChord);

      this.selectedPosition = null;
      this.searchChord = '';
    }
  }

  getChordAt(index: number): string {
    const chordMapping = this.chords.find((c) => c.position === index);

    if (chordMapping) {
      const chord = this.allChords.find((a) => a.id === chordMapping.chordId);
      return chord ? chord.name : '';
    }

    return '';
  }

  removeChord(position: number) {
    this.chords = this.chords.filter((c) => c.position !== position);
  }

  goToEditor() {
    if (this.signinForm.invalid) {
      this.signinForm.markAllAsTouched();
      return;
    }

    this.music.title = this.signinForm.value.title;
    this.music.description = this.signinForm.value.description;
    this.music.releaseDate = this.signinForm.value.releaseDate;
    this.music.lyric.text = this.signinForm.value.lyrics;

    this.prepareEditor();
    this.currentStep = 2;
  }

  submit() {
    this.isLoading = true;
    this.music.lyric.chords = this.chords;

    const token = localStorage.getItem('access_token');

    if (!token) {
      alert('Usuário não autenticado');
      return;
    }

    this.musicService.createMusic(this.music, token).subscribe({
      next: (res) => {
        console.log('Música salva com sucesso!', res);
        alert('Música salva!');
        this.isLoading = false;

        // reset
        this.music = {
          title: '',
          description: '',
          releaseDate: '',
          lyric: { text: '', chords: [] },
        };

        this.textArray = [];
        this.chords = [];
        this.currentStep = 1;

        this.router.navigate(['/home']).then((r) => console.log('Navegação para home', r));
      },
      error: (err) => {
        console.error('Erro ao salvar música', err);
        alert('Erro ao salvar música');
        this.isLoading = false;
      },
    });
  }
}
