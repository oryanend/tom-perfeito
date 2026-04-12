import { Comment } from './comment';

export interface CommentUI extends Comment {
  showReplyBox: boolean;
  newReplyBody: string;
}
