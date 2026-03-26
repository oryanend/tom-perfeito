import { Component, Input, OnInit } from '@angular/core';
import { CommentService } from '../../../core/services/CommentService/comment.service';
import { Comment } from '../../../shared/models/comment';
import { PageResponse } from '../../../shared/models/page-response';
import { CommentUI } from '../../../shared/models/comment-ui';

@Component({
  selector: 'app-comments',
  standalone: false,
  templateUrl: './comments.component.html',
  styleUrl: './comments.component.css',
})
export class CommentsComponent implements OnInit {
  newCommentBody: string = '';
  @Input() musicId!: string;
  @Input() musicAuthorId!: string;
  comments: CommentUI[] = [];

  constructor(private commentService: CommentService) {}

  ngOnInit() {
    this.loadComments();
  }

  loadComments() {
    if (!this.musicId) return;

    this.commentService.getCommentByMusic(this.musicId).subscribe({
      next: (res: PageResponse<Comment>) => {
        // Map para adicionar propriedades extras do frontend
        this.comments = res.content.map((c) => ({
          ...c,
          showReplyBox: false,
          newReplyBody: '',
        })) as CommentUI[];
      },
      error: (err) => console.error('Erro ao carregar comentários', err),
    });
  }

  postComment() {
    if (!this.newCommentBody.trim()) return;

    this.commentService.insertCommentByMusic(this.musicId, this.newCommentBody).subscribe({
      next: (comment: Comment) => {
        // Adiciona o novo comentário com propriedades do frontend
        this.comments.push({
          ...comment,
          showReplyBox: false,
          newReplyBody: '',
        } as CommentUI);
        this.newCommentBody = '';
      },
      error: (err) => console.log(err),
    });
  }

  toggleReplyBox(comment: CommentUI) {
    comment.showReplyBox = !comment.showReplyBox;
  }

  postReply(parentComment: CommentUI) {
    if (!parentComment.newReplyBody?.trim()) return;

    this.commentService
      .insertCommentByMusic(this.musicId, parentComment.newReplyBody, parentComment.id)
      .subscribe({
        next: (reply: Comment) => {
          parentComment.replies = parentComment.replies || [];
          parentComment.replies.push(reply);
          parentComment.newReplyBody = '';
          parentComment.showReplyBox = false;
        },
        error: (err) => console.log(err),
      });
  }

  getTimeAgo(dateString: string): string {
    const now = new Date();
    const past = new Date(dateString);
    const diffMs = now.getTime() - past.getTime();

    const seconds = Math.floor(diffMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (seconds < 60) return 'Just right now';
    if (minutes < 60) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
    if (hours < 24) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
    return `${days} day${days > 1 ? 's' : ''} ago`;
  }
}
