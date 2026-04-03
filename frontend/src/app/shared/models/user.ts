import { MusicPage } from './music-page';

export interface User {
  id: string;
  username: string;
  email: string;
  firstLogin: boolean;
  musics: MusicPage[];
}
