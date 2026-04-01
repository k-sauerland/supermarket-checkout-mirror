import { Routes } from '@angular/router';
import {LoginComponent} from './login.component';
import {AuthGuard} from './guards/auth.guard';
import {Shop} from './features/shop/shop';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'main',
    component: Shop,
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
