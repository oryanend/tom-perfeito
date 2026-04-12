import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../../core/services/UserService/user.service';
import { User } from '../../../shared/models/user';
import { CommentService } from '../../../core/services/CommentService/comment.service';
import { MusicService } from '../../../core/services/MusicService/music.service';
import { Music } from '../../../shared/models/music';
import { CommentMin } from '../../../shared/models/comment-min';

@Component({
  selector: 'app-user-page',
  standalone: false,
  templateUrl: './user-page.component.html',
  styleUrl: './user-page.component.css',
})
export class UserPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private userService = inject(UserService);
  private commentService = inject(CommentService);
  private musicService = inject(MusicService);

  public user?: User;
  commentsCount = 0;
  activeTab: 'music' | 'comments' = 'music';

  // Pagination for musics tab
  public musics: Music[] = [];

  currentPage = 0;
  totalPages = 0;
  pages: number[] = [];

  // Pagination for comments tab
  public comments: CommentMin[] = [];

  commentsPage = 0;
  commentsTotalPages = 0;
  commentsPages: number[] = [];

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      const musicId = params.get('musicId');

      if (id) {
        this.loadUserById(id);
      } else {
        this.loadMe();
      }

      if (musicId) {
        this.commentService.getCommentByMusic(musicId).subscribe((res) => {
          this.commentsCount = res.totalElements;
        });
      }
    });
  }

  setTab(tab: 'music' | 'comments') {
    this.activeTab = tab;

    if (tab === 'comments') {
      this.loadComments();
    }
  }

  loadMe() {
    this.userService.getMe().subscribe((res) => {
      this.user = res;
      this.loadMusics();
    });
  }

  loadUserById(id: string) {
    this.userService.getUserById(id).subscribe((res) => {
      this.user = res;
      this.loadMusics();
    });
  }

  loadMusics(page = 0) {
    this.musicService.getMusicByUserId(this.user!.id, page).subscribe((res) => {
      this.musics = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;
      this.pages = Array.from({ length: this.totalPages }, (_, i) => i);
    });
  }

  loadComments(page = 0) {
    this.commentService.getCommentByUserId(this.user!.id, page).subscribe((res) => {
      this.comments = res.content;
      this.commentsTotalPages = res.totalPages;
      this.commentsPage = res.number;
      this.commentsPages = Array.from({ length: res.totalPages }, (_, i) => i);
    });
  }

  goToPage(page: number) {
    this.loadMusics(page);
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.loadMusics(this.currentPage + 1);
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.loadMusics(this.currentPage - 1);
    }
  }

  goToCommentsPage(page: number) {
    this.loadComments(page);
  }

  nextCommentsPage() {
    if (this.commentsPage < this.commentsTotalPages - 1) {
      this.loadComments(this.commentsPage + 1);
    }
  }

  previousCommentsPage() {
    if (this.commentsPage > 0) {
      this.loadComments(this.commentsPage - 1);
    }
  }
}
