import { NoteName } from '../enums/note-name';
import { Accidental } from '../enums/accidental';

export interface Note {
  id: number;
  name: NoteName;
  accidental: Accidental;
  chords: any[];
}
