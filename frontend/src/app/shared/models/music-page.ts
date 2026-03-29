import { Lyric } from './lyric';
import { UserMin } from './user-min';

export interface MusicPage {
  id: string;
  title: string;
  description: string;

  releaseDate: string;
  createdAt: string;
  updatedAt: string;

  lyric: Lyric;
  createdBy: UserMin;

  comments: Comment[];
}
