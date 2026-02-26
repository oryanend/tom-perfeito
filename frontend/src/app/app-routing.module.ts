import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {WaitingPageComponent} from './waiting-page/waiting-page.component';
import {LoginPageComponent} from './login-page/login-page.component';
import {SignPageComponent} from './sign-page/sign-page.component';

const routes: Routes = [
  { path: 'login', component: LoginPageComponent },
  { path: 'signin', component: SignPageComponent },
  { path: '', component: WaitingPageComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
