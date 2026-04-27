import { Component, ElementRef, HostListener, inject, OnInit, ViewChild } from '@angular/core';
import { MusicService } from '../../../core/services/MusicService/music.service';
import { ChordService } from '../../../core/services/ChordService/chord.service';
import { LyricChord } from '../../../shared/models/lyric-chord';
import { Lyric } from '../../../shared/models/lyric';
import { Chord } from '../../../shared/models/chord';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LoginModalComponent } from '../../../shared/components/login-modal/login-modal.component';
import { ActivatedRoute, Router } from '@angular/router';
import { NetworkError } from '../../../core/errors/network/network-error';
import { ApiError } from '../../../core/errors/api/api-errors';

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
  private activatedRouter = inject(ActivatedRoute);
  private router = inject(Router);

  @ViewChild('loginModal') loginModal!: LoginModalComponent;
  @ViewChild('popup') popupRef!: ElementRef;

  currentStep = 1;
  signinForm!: FormGroup;
  isLoading = false;
  private searchTimeout!: ReturnType<typeof setTimeout>;

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

  popupX = 0;
  popupY = 0;

  // Pop Pagination
  currentPageChords = 0;
  totalPagesChords = 0;
  pagesChords: number[] = [];

  chords: LyricChord[] = [];
  allChords: Chord[] = [];
  filteredChords: Chord[] = [];
  searchChord = '';

  // AlertType
  alertType: 'success' | 'warning' | 'error' | null = null;
  alertMessage = '';

  isEditMode = false;
  musicId: string | null = null;

  ngOnInit() {
    this.loadChords();
    this.clearAlert();

    this.signinForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      releaseDate: ['', Validators.required],
      lyrics: ['', Validators.required],
    });

    this.activatedRouter.paramMap.subscribe((params) => {
      const id = params.get('id');

      if (id) {
        this.isEditMode = true;
        this.musicId = id;
        this.loadMusic(id);
      }
    });
  }

  loadMusic(id: string) {
    this.musicService.getById(id).subscribe((res) => {
      this.music = {
        title: res.title,
        description: res.description,
        releaseDate: res.releaseDate.split('T')[0],
        lyric: {
          text: res.lyric?.text || '',
          chords: res.lyric?.chords || [],
        },
      };

      this.chords = [...this.music.lyric.chords];

      this.signinForm.patchValue({
        title: this.music.title,
        description: this.music.description,
        releaseDate: this.music.releaseDate,
        lyrics: this.music.lyric.text,
      });

      this.prepareEditor();
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

  loadChords(page = 0) {
    this.chordService.getAll(page).subscribe({
      next: (res) => {
        this.allChords = res.content;
        this.filteredChords = this.allChords;

        this.currentPageChords = res.number;
        this.totalPagesChords = res.totalPages;

        this.pagesChords = Array(this.totalPagesChords)
          .fill(0)
          .map((_, i) => i);
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
    clearTimeout(this.searchTimeout);

    this.searchTimeout = setTimeout(() => {
      if (!this.searchChord.trim()) {
        this.loadChords(0);
        return;
      }

      this.chordService.searchByName(this.searchChord).subscribe({
        next: (res) => {
          this.filteredChords = res;
          this.totalPagesChords = 1;
          this.currentPageChords = 0;
        },
        error: (err) => console.error(err),
      });
    }, 300);
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
    if (!this.popupRef) return;

    const clickedInside = this.popupRef.nativeElement.contains(event.target);

    if (!clickedInside) {
      this.selectedPosition = null;
    }
  }

  selectChord(chord: Chord) {
    if (this.selectedPosition !== null) {
      this.chords = this.chords.filter((c) => c.position !== this.selectedPosition);

      this.chords.push({
        chordId: chord.id,
        position: this.selectedPosition,
        name: chord.name,
      });

      this.selectedPosition = null;
      this.searchChord = '';
    }
  }

  getChordAt(index: number): string {
    const chordMapping = this.chords.find((c) => c.position === index);
    return chordMapping?.name || '';
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

    const request = this.isEditMode
      ? this.musicService.updateMusic(this.musicId!, this.music)
      : this.musicService.createMusic(this.music, token);

    request.subscribe({
      next: () => {
        this.isLoading = false;

        this.showAlert(
          'success',
          this.isEditMode ? 'Music updated successfully! ✏️' : 'Music created successfully! 🎉'
        );

        if (!this.isEditMode) {
          this.resetForm();
        }
      },
      error: () => {
        this.isLoading = false;
        this.showAlert('error', 'Something went wrong.');
      },
    });
  }

  resetForm() {
    this.signinForm.reset();

    this.music = {
      title: '',
      description: '',
      releaseDate: '',
      lyric: {
        text: '',
        chords: [],
      },
    };

    this.textArray = [];
    this.chords = [];
    this.filteredChords = [...this.allChords];
    this.currentStep = 1;
    this.searchChord = '';
    this.selectedPosition = null;
    this.hoverIndex = null;
  }

  goToPageChords(page: number) {
    this.loadChords(page);
  }

  nextPageChords() {
    if (this.currentPageChords < this.totalPagesChords - 1) {
      this.loadChords(this.currentPageChords + 1);
    }
  }

  previousPageChords() {
    if (this.currentPageChords > 0) {
      this.loadChords(this.currentPageChords - 1);
    }
  }

  get visiblePagesChords(): number[] {
    const range = 2;
    const start = Math.max(0, this.currentPageChords - range);
    const end = Math.min(this.totalPagesChords, this.currentPageChords + range + 1);

    return Array.from({ length: end - start }, (_, i) => start + i);
  }

  deleteMusic() {
    if (!this.musicId) return;

    const confirmDelete = confirm('Are you sure you want to delete this music?');

    if (!confirmDelete) return;

    this.musicService.deleteMusic(this.musicId).subscribe({
      next: () => {
        this.showAlert('success', 'Music deleted successfully!');
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error(err);

        if (err instanceof NetworkError) {
          this.showAlert('error', 'Unable to connect to the server.');
        }

        if (err instanceof ApiError) {
          this.showAlert('warning', err.message);
        }
      },
    });
  }
}
