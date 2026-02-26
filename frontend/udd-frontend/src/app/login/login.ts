import { Component } from '@angular/core';
import { LoginDetails } from '../models/login-details.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  loginData: LoginDetails = {
    email: '',
    password: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    console.log('Podaci spremni za slanje:', this.loginData);
    this.authService.login(this.loginData).subscribe({
      next: (token: string) => {
        console.log('Login uspešan, token primljen');

        localStorage.setItem("udd_token", token);

        this.authService.refreshToken();

        this.router.navigate(['/search-reports'])
      }
    })
    // Ovde ide poziv ka servisu: this.authService.login(this.loginData)...
  }
}
