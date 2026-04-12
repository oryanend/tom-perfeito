import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../../core/services/UserService/user.service';
import { User } from '../../../shared/models/user';
import { CommentService } from '../../../core/services/CommentService/comment.service';
import { MusicService } from '../../../core/services/MusicService/music.service';
import { Music } from '../../../shared/models/music';
import { CommentMin } from '../../../shared/models/comment-min';
import { PaginationState } from '../../../shared/models/pagination-state';

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

  public musicPagination: PaginationState<Music> = {
    content: [],
    currentPage: 0,
    totalPages: 0,
    pages: [],
  };

  public commentPagination: PaginationState<CommentMin> = {
    content: [],
    currentPage: 0,
    totalPages: 0,
    pages: [],
  };

  loadMusics(page = 0) {
    this.musicService.getMusicByUserId(this.user!.id, page).subscribe((res) => {
      this.musicPagination = {
        content: res.content,
        currentPage: res.number,
        totalPages: res.totalPages,
        pages: Array.from({ length: res.totalPages }, (_, i) => i),
      };
    });
  }

  loadComments(page = 0) {
    this.commentService.getCommentByUserId(this.user!.id, page).subscribe((res) => {
      this.commentPagination = {
        content: res.content,
        currentPage: res.number,
        totalPages: res.totalPages,
        pages: Array.from({ length: res.totalPages }, (_, i) => i),
      };
    });
  }

  goToPage(type: 'music' | 'comments', page: number) {
    if (type === 'music') {
      this.loadMusics(page);
    } else {
      this.loadComments(page);
    }
  }

  nextPage(type: 'music' | 'comments') {
    const state = type === 'music' ? this.musicPagination : this.commentPagination;

    if (state.currentPage < state.totalPages - 1) {
      this.goToPage(type, state.currentPage + 1);
    }
  }

  previousPage(type: 'music' | 'comments') {
    const state = type === 'music' ? this.musicPagination : this.commentPagination;

    if (state.currentPage > 0) {
      this.goToPage(type, state.currentPage - 1);
    }
  }
}
