import { Injectable, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly AUTH_API = 'http://localhost:8080/api/v1/auth/';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    console.log('Final URL being called:', this.AUTH_API + 'login');
    return this.http.post(this.AUTH_API + 'login', {
      username: username,
      password: password
    }).pipe(
      tap((response: any) => {
        if (response.token) {
          localStorage.setItem('id_token', response.token)
        }
      })
    );
  }
  isLoggedIn(): boolean {
    return !!localStorage.getItem('id_token');
  }
  logout() {
    localStorage.removeItem('id_token');
  }
}
