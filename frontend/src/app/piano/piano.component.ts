import { Component } from '@angular/core';
import { NoteServiceService } from '../services/NoteService/note-service.service';
import { Note } from '../models/note';
import { NoteName } from '../enums/note-name';
import { Accidental } from '../enums/accidental';
import {Chord} from '../models/chord';
import {ChordService} from '../services/ChordService/chord.service';

@Component({
  selector: 'app-piano',
  standalone: false,
  templateUrl: './piano.component.html',
  styleUrl: './piano.component.css'
})

export class PianoComponent {
  selectedKeys = new Set<string>();
  chords: Chord[] = [];

  keys = [
    { name: NoteName.C, accidental: Accidental.NATURAL, type: 'white' },
    { name: NoteName.C, accidental: Accidental.SHARP, type: 'black' },
    { name: NoteName.D, accidental: Accidental.NATURAL, type: 'white' },
    { name: NoteName.D, accidental: Accidental.SHARP, type: 'black' },
    { name: NoteName.E, accidental: Accidental.NATURAL, type: 'white' },
    { name: NoteName.F, accidental: Accidental.NATURAL, type: 'white' },
    { name: NoteName.F, accidental: Accidental.SHARP, type: 'black' },
    { name: NoteName.G, accidental: Accidental.NATURAL, type: 'white' },
    { name: NoteName.G, accidental: Accidental.SHARP, type: 'black' },
    { name: NoteName.A, accidental: Accidental.NATURAL, type: 'white' },
    { name: NoteName.A, accidental: Accidental.SHARP, type: 'black' },
    { name: NoteName.B, accidental: Accidental.NATURAL, type: 'white' }
  ];

  constructor(private noteService: NoteServiceService, private chordService: ChordService) {}

  private getKeyId(name: NoteName, accidental: Accidental): string {
    return `${name}-${accidental}`;
  }

  onKeyClick(key: any) {
    const keyId = this.getKeyId(key.name, key.accidental);

    if (this.selectedKeys.has(keyId)) {
      this.selectedKeys.delete(keyId);
    } else {
      this.selectedKeys.add(keyId);
    }

    this.searchChords();
  }

  isSelected(key: any): boolean {
    return this.selectedKeys.has(
      this.getKeyId(key.name, key.accidental)
    );
  }

  private getSelectedNoteStrings(): string[] {
    return Array.from(this.selectedKeys).map(keyId => {
      const [name, accidental] = keyId.split('-');

      if (accidental === Accidental.SHARP) return name + '#';
      if (accidental === Accidental.FLAT) return name + 'b';
      return name;
    });
  }

  private getSelectedNotes(): { name: NoteName; accidental: Accidental }[] {
    return Array.from(this.selectedKeys).map(keyId => {
      const [name, accidental] = keyId.split('-');

      return {
        name: name as NoteName,
        accidental: accidental as Accidental
      };
    });
  }

  private searchChords() {
    const notes = this.getSelectedNoteStrings();

    if (notes.length < 2) {
      this.chords = [];
      return;
    }

    this.chordService.searchByNotes(notes)
      .subscribe(chords => {
        this.chords = chords;
      });
  }

  getDisplayNote(note: { name: NoteName; accidental: Accidental }): string {
    if (note.accidental === Accidental.SHARP) return note.name + '#';
    if (note.accidental === Accidental.FLAT) return note.name + 'b';
    return note.name;
  }
}
