import { Injectable, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly AUTH_API = 'http://localhost:8080/api/v1/auth/';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
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
  register(username: string, password: string): Observable<any> {
    return this.http.post(this.AUTH_API + 'register', {
      username: username,
      password: password},
      {responseType: 'text'}
      );
    }
  isLoggedIn(): boolean {
    return !!localStorage.getItem('id_token');
  }
  logout() {
    localStorage.removeItem('id_token');
  }
}
