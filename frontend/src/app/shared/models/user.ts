import { MusicPage } from './music-page';
import { CommentMin } from './comment-min';

export interface User {
  id: string;
  username: string;
  email: string;
  firstLogin: boolean;
  musics: MusicPage[];
  comments: CommentMin[];
}
