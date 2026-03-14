import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WaitingPageComponent } from './features/waiting/waiting-page/waiting-page.component';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { FooterComponent } from './shared/components/footer/footer.component';
import { LoginPageComponent } from './features/auth/login-page/login-page.component';
import { SignUpPageComponent } from './features/auth/sign-up-page/sign-up-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { HomePageComponent } from './features/home/home-page/home-page.component';
import { PianoComponent } from './features/home/piano/piano.component';
import { StatusPageComponent } from './features/status/status-page/status-page.component';
import { MusicsPageComponent } from './features/musics/musics-page/musics-page.component';
import { AuthErrorInterceptor } from './core/interceptors/auth.interceptor';

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
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, ReactiveFormsModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthErrorInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
