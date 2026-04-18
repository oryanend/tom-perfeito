import { Comment } from './comment';

export interface CommentUI extends Comment {
  expanded?: boolean;
  showReplyBox?: boolean;
  newReplyBody?: string;
}
