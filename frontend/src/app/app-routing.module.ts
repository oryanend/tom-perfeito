import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {WaitingPageComponent} from './waiting-page/waiting-page.component';
import {LoginPageComponent} from './login-page/login-page.component';
import {SignPageComponent} from './sign-page/sign-page.component';
import {HomePageComponent} from './home-page/home-page.component';
import {StatusPageComponent} from './status-page/status-page.component';
import {MusicsPageComponent} from './musics-page/musics-page.component';

const routes: Routes = [
  { path: 'login', component: LoginPageComponent },
  { path: 'signin', component: SignPageComponent },
  { path: 'home', component: HomePageComponent },
  { path: 'status', component: StatusPageComponent },
  { path: 'musics', component: MusicsPageComponent },
  { path: '', component: WaitingPageComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
