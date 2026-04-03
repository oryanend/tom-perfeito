import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../../core/services/UserService/user.service';
import { User } from '../../../shared/models/user';
import { CommentService } from '../../../core/services/CommentService/comment.service';
import { MusicService } from '../../../core/services/MusicService/music.service';
import { Music } from '../../../shared/models/music';

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

  currentPage = 0;
  totalPages = 0;
  pages: number[] = [];

  public musics: Music[] = [];

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
      console.log('PAGE:', page, res);

      this.musics = res.content;
      this.totalPages = res.totalPages;
      this.currentPage = res.number;

      this.pages = Array.from({ length: this.totalPages }, (_, i) => i);
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
}
