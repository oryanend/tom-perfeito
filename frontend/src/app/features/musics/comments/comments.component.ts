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

  // Pagination
  currentPage = 0;
  totalPages = 0;
  pages: number[] = [];

  loggedUserUsername!: string;

  editingCommentId: number | null = null;
  editBody = '';

  ngOnInit() {
    this.loadComments();
    this.loggedUserUsername = this.getUsernameFromToken()!;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['musicId'] && !changes['musicId'].firstChange) {
      this.loadComments();
    }
  }

  loadComments(page = 0) {
    if (!this.musicId) return;

    this.comments = [];

    this.commentService.getCommentByMusic(this.musicId, page).subscribe({
      next: (res: PageResponse<Comment>) => {
        this.comments = res.content
          .filter((c) => !c.parentId)
          .map((c) => ({
            ...c,
            showReplyBox: false,
            newReplyBody: '',
            expanded: false,
            likedByUser: false,
          })) as CommentUI[];

        this.currentPage = res.number;
        this.totalPages = res.totalPages;

        this.pages = Array(this.totalPages)
          .fill(0)
          .map((x, i) => i);
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
        this.clearAlert();
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
      .insertCommentByMusic(this.musicId, parentComment.newReplyBody!, parentComment.id)
      .subscribe({
        next: (reply: Comment) => {
          parentComment.replies = parentComment.replies || [];

          parentComment.replies.push({
            ...(reply as CommentUI),
            expanded: false,
            showReplyBox: false,
            newReplyBody: '',
          } as CommentUI);

          parentComment.newReplyBody = '';
          parentComment.showReplyBox = false;
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

  goToPage(page: number) {
    this.loadComments(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  nextPage() {
    if (this.currentPage >= this.totalPages - 1) return;
    this.loadComments(this.currentPage + 1);
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.loadComments(this.currentPage - 1);
    }
  }

  addLike(commentId: number, musicId: string) {
    this.commentService.addLike(commentId, musicId).subscribe({
      next: (res) => {
        this.comments = this.comments.map((c) =>
          c.id === commentId
            ? {
                ...c,
                likes: res.likes,
                likedByUser: res.likedByUser,
              }
            : c
        );
      },
      error: (err) => {
        if (err instanceof AuthError) {
          this.loginModal.openLoginModal();
        } else {
          console.error(err);
        }
      },
    });
  }

  deleteComment(commentId: number, musicId: string) {
    this.commentService.deleteCommentById(musicId, commentId).subscribe({
      next: () => {
        this.comments = this.comments.filter((c) => c.id !== commentId);
        this.clearAlert();
      },
      error: (err) => {
        console.error(err);

        if (err instanceof NetworkError) {
          this.showAlert('error', 'Unable to connect to the server. Please try again later.');
        }

        if (err instanceof ApiError) {
          this.showAlert('warning', err.message);
        }
      },
    });
  }

  startEdit(comment: CommentUI) {
    this.editingCommentId = comment.id;
    this.editBody = comment.body;
  }

  saveEdit(comment: CommentUI) {
    const newBody = this.editBody.trim();
    const originalBody = comment.body.trim();

    if (newBody === originalBody) {
      this.editingCommentId = null;
      this.editBody = '';
      return;
    }

    if (newBody.length === 0) {
      return this.deleteComment(comment.id, this.musicId);
    }

    this.commentService.updateCommentById(this.musicId, comment.id, newBody).subscribe({
      next: (updated) => {
        this.comments = this.comments.map((c) =>
          c.id === comment.id ? { ...c, body: updated.body, updatedAt: updated.updatedAt } : c
        );

        this.editingCommentId = null;
        this.editBody = '';
      },
      error: (err) => console.error(err),
    });
  }

  private getUsernameFromToken(): string | null {
    const token = localStorage.getItem('access_token');
    if (!token) return null;

    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.username;
  }
}
