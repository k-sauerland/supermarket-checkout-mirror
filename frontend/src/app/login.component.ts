import {Component} from '@angular/core';
import {AuthService} from './core/service/auth.service';
import {Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="flex min-h-full bg-gray-100 flex-col justify-center px-6 py-12 lg:px-8 ">
      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <!--
        <img src="https://tailwindcss.com/plus-assets/img/logos/mark.svg?color=indigo&shade=500" alt="Your Company" class="mx-auto h-10 w-auto" />
        -->
        <h2 class="mt-10 text-center text-2xl/9 font-bold tracking-tight text-indigo-400">Sign in to your account</h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form (ngSubmit)="login()" class="space-y-6">
          <div>
            <label for="username" class="block text-sm/6 font-medium text-indigo-400">Username</label>
            <div class="mt-2">
              <input type="text" name="username" [(ngModel)]="credentials.username" required autocomplete="username"
                     class="block w-full rounded-md bg-gray-600/5 px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-white/10 placeholder:text-gray-500 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-500 sm:text-sm/6"/>
            </div>
          </div>

          <div>
            <div class="flex items-center justify-between">
              <label for="password" class="block text-sm/6 font-medium text-indigo-400">Password</label>
              <div class="text-sm">
                <a href="#" class="font-semibold text-indigo-400 hover:text-indigo-300">Forgot password?</a>
              </div>
            </div>
            <div class="mt-2">
              <input type="password" name="password" [(ngModel)]="credentials.password" required autocomplete="current-password"
                     class="block w-full rounded-md bg-gray-600/5 px-3 py-1.5 text-base text-gray-900 outline-1 -outline-offset-1 outline-white/10 placeholder:text-gray-500 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-500 sm:text-sm/6"/>
            </div>
          </div>

          <div>
            <button type="submit"
                    class="flex w-full justify-center rounded-md bg-indigo-500 px-3 py-1.5 text-sm/6 font-semibold text-white hover:bg-indigo-400 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500">
              Sign in
            </button>
          </div>
        </form>

        <p class="mt-10 text-center text-sm/6 text-gray-400">
          Not a member?
          <a href="#" class="font-semibold text-indigo-400 hover:text-indigo-300">Start a 14 day free trial</a>
        </p>
      </div>
    </div>`})
export class LoginComponent {
  isLoginMode = true;
  credentials = {
    username: '',
    password: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login() {
    console.log('Button clicked! Data:', this.credentials);
    if (!this.credentials.username || !this.credentials.password) {
      alert('Please enter both username and password');
      return;
    }

    this.authService.login(this.credentials.username, this.credentials.password).subscribe({
      next: (response: any) => {
        const token = response.token || response.id_token || response.accessToken;

        if (token) {
          localStorage.setItem('id_token', token);
          this.router.navigate(['/main']);
        }
      },
      error: (err) => {
        console.error('HTTP Error during login:', err);
        alert('Login failed. Check the console for details.')
      }
    });
  }
}

