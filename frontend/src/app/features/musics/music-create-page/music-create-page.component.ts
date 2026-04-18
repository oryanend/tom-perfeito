import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { MusicService } from '../../../core/services/MusicService/music.service';
import { ChordService } from '../../../core/services/ChordService/chord.service';
import { LyricChord } from '../../../shared/models/lyric-chord';
import { Lyric } from '../../../shared/models/lyric';
import { Chord } from '../../../shared/models/chord';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NetworkError } from '../../../core/errors/network/network-error';
import { InvalidRequestError } from '../../../core/errors/auth/invalid-request-error';
import { LoginModalComponent } from '../../../shared/components/login-modal/login-modal.component';

@Component({
  selector: 'app-music-create-page',
  standalone: false,
  templateUrl: './music-create-page.component.html',
  styleUrl: './music-create-page.component.css',
})
export class MusicCreatePageComponent implements OnInit {
  private musicService = inject(MusicService);
  private chordService = inject(ChordService);
  private fb = inject(FormBuilder);

  @ViewChild('loginModal') loginModal!: LoginModalComponent;

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

  // AlertType
  alertType: 'success' | 'warning' | 'error' | null = null;
  alertMessage = '';

  ngOnInit() {
    this.loadChords();
    this.clearAlert();

    this.signinForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      releaseDate: ['', Validators.required],
      lyrics: ['', Validators.required],
    });
  }

  showAlert(type: 'success' | 'warning' | 'error', message: string) {
    this.alertType = type;
    this.alertMessage = message;
  }

  clearAlert() {
    this.alertType = null;
    this.alertMessage = '';
  }

  loadChords() {
    this.chordService.getAll().subscribe({
      next: (res) => {
        this.allChords = res.content;
        this.filteredChords = this.allChords;
      },
      error: (err) => console.error('Failed to load chords', err),
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
    this.clearAlert();
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
      this.loginModal.openLoginModal();
      return;
    }

    this.musicService.createMusic(this.music, token).subscribe({
      next: (res) => {
        this.isLoading = false;

        this.clearAlert();
        console.log('Music successfully created!', res);

        this.showAlert('success', 'Music successfully created! 🎉');

        this.signinForm.reset();

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
      },
      error: (err) => {
        this.isLoading = false;

        if (err instanceof NetworkError) {
          this.showAlert('error', 'Unable to connect to the server. Please try again later.');
        }

        if (err instanceof InvalidRequestError) {
          this.showAlert('warning', err.message);
        }
      },
    });
  }
}
