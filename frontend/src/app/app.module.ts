import { ErrorHandler, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WaitingPageComponent } from './features/waiting/waiting-page/waiting-page.component';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { FooterComponent } from './shared/components/footer/footer.component';
import { LoginPageComponent } from './features/auth/login-page/login-page.component';
import { SignUpPageComponent } from './features/auth/sign-up-page/sign-up-page.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { HomePageComponent } from './features/home/home-page/home-page.component';
import { PianoComponent } from './features/home/piano/piano.component';
import { StatusPageComponent } from './features/status/status-page/status-page.component';
import { MusicsPageComponent } from './features/musics/musics-page/musics-page.component';
import { AuthErrorInterceptor } from './core/interceptors/auth.interceptor';
import { WelcomeModalComponent } from './shared/components/welcome-modal/welcome-modal.component';
import { GlobalErrorHandler } from './core/handlers/global-error-handler';
import { HttpErrorInterceptor } from './core/interceptors/http-error.interceptor';
import { SearchBarComponent } from './shared/components/search-bar/search-bar/search-bar.component';
import { MusicComponent } from './features/musics/music/music.component';
import { CommentsComponent } from './features/musics/comments/comments.component';
import { UserPageComponent } from './features/user/user-page/user-page.component';
import { MusicCreatePageComponent } from './features/musics/music-create-page/music-create-page.component';
import { TimeAgoPipe } from './shared/utils/time-ago.pipe';

@NgModule({
  declarations: [
    AppComponent,
    WaitingPageComponent,
    NavbarComponent,
    FooterComponent,
    LoginPageComponent,
    SignUpPageComponent,
    HomePageComponent,
    PianoComponent,
    StatusPageComponent,
    MusicsPageComponent,
    WelcomeModalComponent,
    SearchBarComponent,
    MusicComponent,
    CommentsComponent,
    UserPageComponent,
    MusicCreatePageComponent,
    TimeAgoPipe,
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, ReactiveFormsModule, FormsModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthErrorInterceptor,
      multi: true,
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
