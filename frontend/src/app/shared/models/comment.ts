import { UserMin } from './user-min';
import { Music } from './music';

export interface Comment {
  id: number;
  body: string;
  likes: number;

  createdAt: string;
  updatedAt: string;

  parentId: number | null;

  author: UserMin;
  music: Music;

  replies: Comment[];
}
