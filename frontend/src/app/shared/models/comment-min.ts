import { UserMin } from './user-min';

export interface CommentMin {
  id: number;
  body: string;
  likes: number;
  createdAt: string;
  musicId: string;
  author: UserMin;
}
