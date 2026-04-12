import { Component, Input, OnInit, inject, ViewChild } from '@angular/core';
import { CommentService } from '../../../core/services/CommentService/comment.service';
import { Comment } from '../../../shared/models/comment';
import { PageResponse } from '../../../shared/models/page-response';
import { CommentUI } from '../../../shared/models/comment-ui';
import { AuthError } from '../../../core/errors/auth/auth-error';
import { Modal } from 'bootstrap';
import { Router } from '@angular/router';
import { LoginModalComponent } from '../../../shared/components/login-modal/login-modal.component';

@Component({
  selector: 'app-comments',
  standalone: false,
  templateUrl: './comments.component.html',
  styleUrl: './comments.component.css',
})
export class CommentsComponent implements OnInit {
  private commentService = inject(CommentService);
  private router = inject(Router);

  @ViewChild('loginModal') loginModal!: LoginModalComponent;
  @Input() musicId!: string;
  @Input() musicAuthorId!: string;

  newCommentBody = '';
  modalInstance!: Modal;
  comments: CommentUI[] = [];

  ngOnInit() {
    this.loadComments();
  }

  loadComments() {
    if (!this.musicId) return;

    this.commentService.getCommentByMusic(this.musicId).subscribe({
      next: (res: PageResponse<Comment>) => {
        this.comments = res.content
          .filter((c) => !c.parentId)
          .map((c) => ({
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
        this.comments.push({
          ...comment,
          showReplyBox: false,
          newReplyBody: '',
        } as CommentUI);
        this.newCommentBody = '';
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
}
