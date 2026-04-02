import { Component } from '@angular/core';
import { AuthService } from './core/service/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgForm } from '@angular/forms'

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  isLoginMode = true;
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onHandleMode() {
    this.isLoginMode = !this.isLoginMode;}

  onSubmit(form: NgForm) {
    const {username,password, confirmPassword} = form.value
    if (!username || !password) {
      alert('Please fill in all required fields');
      return;
    }

    if (this.isLoginMode) {
      this.performLogin(username,password);
    } else {
      if (password !== confirmPassword) {
        alert('Passwords do not match');
        return}
      this.performRegistration(username,password);
    }
  }

  private performLogin(username: string, password: string) {
    this.authService.login(username, password).subscribe({
      next: (response: any) => {
        const token = response.token || response.id_token || response.accessToken;
        if (token) {
          localStorage.setItem('id_token', token);
          this.router.navigate(['/main']);
        }
      },
      error: (err) => {
        console.error('Login Error:', err);
        alert('Login failed. Please check your credentials.');
      }
    });
  }

  private performRegistration(username: string, password: string) {
    this.authService.register(username, password).subscribe({
      next: () => {
        alert('Registration successful! Please log in.');
        this.isLoginMode = true;
      },
      error: (err) => {
        console.log(username,password)
        console.error('Registration Error:', err);
        alert('Registration failed. Username might already be taken.');
      }
    });
  }
}
