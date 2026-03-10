import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WaitingPageComponent } from './waiting-page/waiting-page.component';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { SignPageComponent } from './sign-page/sign-page.component';
import {ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { HomePageComponent } from './home-page/home-page.component';
import { PianoComponent } from './piano/piano.component';
import { StatusPageComponent } from './status-page/status-page.component';
import { MusicsPageComponent } from './musics-page/musics-page.component';
import {AuthErrorInterceptor} from './interceptors/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    WaitingPageComponent,
    NavbarComponent,
    FooterComponent,
    LoginPageComponent,
    SignPageComponent,
    HomePageComponent,
    PianoComponent,
    StatusPageComponent,
    MusicsPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthErrorInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
