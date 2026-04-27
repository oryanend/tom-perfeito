import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './features/auth/login-page/login-page.component';
import { SignUpPageComponent } from './features/auth/sign-up-page/sign-up-page.component';
import { HomePageComponent } from './features/home/home-page/home-page.component';
import { StatusPageComponent } from './features/status/status-page/status-page.component';
import { MusicsPageComponent } from './features/musics/musics-page/musics-page.component';
import { MusicComponent } from './features/musics/music/music.component';
import { UserPageComponent } from './features/user/user-page/user-page.component';
import { MusicCreatePageComponent } from './features/musics/music-create-page/music-create-page.component';

const routes: Routes = [
  { path: 'login', component: LoginPageComponent },
  { path: 'signup', component: SignUpPageComponent },
  { path: '', component: HomePageComponent },
  { path: 'status', component: StatusPageComponent },
  { path: 'musics', component: MusicsPageComponent },
  { path: 'music/:id', component: MusicComponent },
  { path: 'user/:id', component: UserPageComponent },
  { path: 'create/music', component: MusicCreatePageComponent },
  { path: 'music/edit/:id', component: MusicCreatePageComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
