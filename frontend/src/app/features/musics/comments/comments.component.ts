import {
  Component,
  inject,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { CommentService } from '../../../core/services/CommentService/comment.service';
import { Comment } from '../../../shared/models/comment';
import { PageResponse } from '../../../shared/models/page-response';
import { CommentUI } from '../../../shared/models/comment-ui';
import { AuthError } from '../../../core/errors/auth/auth-error';
import { LoginModalComponent } from '../../../shared/components/login-modal/login-modal.component';
import { NetworkError } from '../../../core/errors/network/network-error';
import { InvalidRequestError } from '../../../core/errors/auth/invalid-request-error';
import { ApiError } from '../../../core/errors/api/api-errors';

@Component({
  selector: 'app-comments',
  standalone: false,
  templateUrl: './comments.component.html',
  styleUrl: './comments.component.css',
})
export class CommentsComponent implements OnInit, OnChanges {
  private commentService = inject(CommentService);

  @ViewChild('loginModal') loginModal!: LoginModalComponent;
  @Input() musicId!: string;
  @Input() musicAuthorId!: string;

  newCommentBody = '';
  comments: CommentUI[] = [];
  readMoreLimitComment = 100;

  // AlertType
  alertType: 'success' | 'warning' | 'error' | null = null;
  alertMessage = '';

  ngOnInit() {
    this.loadComments();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['musicId'] && !changes['musicId'].firstChange) {
      this.loadComments();
    }
  }

  loadComments() {
    if (!this.musicId) return;

    this.comments = [];

    this.commentService.getCommentByMusic(this.musicId).subscribe({
      next: (res: PageResponse<Comment>) => {
        this.comments = res.content
          .filter((c) => !c.parentId)
          .map((c) => ({
            ...c,
            showReplyBox: false,
            newReplyBody: '',
            expanded: false,
          })) as CommentUI[];
      },
      error: (err) => console.error('Failed to load comments', err),
    });
  }

  postComment() {
    if (!this.newCommentBody.trim()) return;

    this.commentService.insertCommentByMusic(this.musicId, this.newCommentBody).subscribe({
      next: (comment: Comment) => {
        this.comments.push({
          ...comment,
          showReplyBox: false,
          newReplyBody: '',
          expanded: false,
        } as CommentUI);
        this.newCommentBody = '';
      },
      error: (err) => {
        if (err instanceof AuthError) {
          this.loginModal.openLoginModal();
        } else {
          console.error(err);
        }

        if (err instanceof NetworkError) {
          this.showAlert('error', 'Unable to connect to the server. Please try again later.');
        }

        if (err instanceof ApiError) {
          this.showAlert('warning', err.message);
        }
      },
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
        error: (err) => {
          if (err instanceof NetworkError) {
            this.showAlert('error', 'Unable to connect to the server. Please try again later.');
          }

          if (err instanceof ApiError) {
            this.showAlert('warning', err.message);
          }
        },
      });
  }

  toggleExpand(comment: CommentUI) {
    comment.expanded = !comment.expanded;
  }

  getShortText(text: string, limit: number = this.readMoreLimitComment): string {
    if (!text) return '';
    return text.length > limit ? text.substring(0, limit) + '...' : text;
  }

  showAlert(type: 'success' | 'warning' | 'error', message: string) {
    this.alertType = type;
    this.alertMessage = message;
  }

  clearAlert() {
    this.alertType = null;
    this.alertMessage = '';
  }
}
