import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavbarComponent } from './navbar/navbar.component';
import { WaitingPageComponent } from './waiting-page/waiting-page.component';

const routes: Routes = [
  { path: 'navbar', component: NavbarComponent },
  { path: '', component: WaitingPageComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
