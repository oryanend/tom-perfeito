import { Component, OnInit, inject } from '@angular/core';
import { MusicService } from '../../../core/services/MusicService/music.service';
import { ActivatedRoute } from '@angular/router';
import { MusicPage } from '../../../shared/models/music-page';
import { ChordService } from '../../../core/services/ChordService/chord.service';
import { Chord } from '../../../shared/models/chord';
import { CommentService } from '../../../core/services/CommentService/comment.service';

@Component({
  selector: 'app-music',
  standalone: false,
  templateUrl: './music.component.html',
  styleUrl: './music.component.css',
})
export class MusicComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private musicService = inject(MusicService);
  private chordService = inject(ChordService);
  private commentService = inject(CommentService);

  music!: MusicPage;
  chordMap: Record<number, Chord> = {};
  commentsCount = 0;

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');

      if (id) {
        this.loadMusic(id);

        this.musicService.getById(id).subscribe((response) => {
          this.music = response;
        });

        this.commentService.getCommentByMusic(id).subscribe((res) => {
          this.commentsCount = res.totalElements;
        });
      }

      this.chordService.getAll().subscribe((response) => {
        response.content.forEach((c) => {
          this.chordMap[c.id] = c;
        });
      });
    });
  }

  loadMusic(id: string) {
    this.musicService.getById(id).subscribe((response) => {
      this.music = response;
    });
  }

  getChordName(id: number): string {
    return this.chordMap[id]?.name || '?';
  }

  getProcessedLyric() {
    if (!this.music?.lyric) return [];

    const text = this.music.lyric.text;
    const chords = this.music.lyric.chords;

    const lines = text.split('\n');
    let currentIndex = 0;

    return lines.map((line) => {
      const lineChords = chords
        .filter((c) => c.position >= currentIndex && c.position < currentIndex + line.length)
        .map((c) => ({
          position: c.position - currentIndex,
          chord: this.getChordName(c.chordId),
        }));

      currentIndex += line.length + 1;

      return {
        text: line,
        chords: lineChords,
      };
    });
  }

  hasChordsLoaded(): boolean {
    return Object.keys(this.chordMap).length > 0;
  }

  getUniqueChords(): string[] {
    if (!this.music?.lyric) return [];

    const uniqueIds = new Set<number>();

    this.music.lyric.chords.forEach((c) => {
      uniqueIds.add(c.chordId);
    });

    return Array.from(uniqueIds).map((id) => this.getChordName(id));
  }
}
