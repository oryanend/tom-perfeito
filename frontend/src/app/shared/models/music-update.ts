export interface MusicUpdate {
  title: string;
  description: string;
  releaseDate: string;
  lyric: {
    text: string;
    chords: {
      chordId: number;
      position: number;
      name?: string;
    }[];
  };
}
