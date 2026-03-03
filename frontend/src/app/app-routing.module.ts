import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {WaitingPageComponent} from './waiting-page/waiting-page.component';
import {LoginPageComponent} from './login-page/login-page.component';
import {SignPageComponent} from './sign-page/sign-page.component';
import {HomePageComponent} from './home-page/home-page.component';

const routes: Routes = [
  { path: 'login', component: LoginPageComponent },
  { path: 'signin', component: SignPageComponent },
  { path: 'home', component: HomePageComponent },
  { path: '', component: WaitingPageComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
