import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RouterLink, RouterLinkActive } from '@angular/router'; // Uvezi ovo

@Component({
  selector: 'app-navbar',
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {

    public isLoggedIn: boolean = false;

  constructor(public authService: AuthService, private router: Router) {}

ngOnInit() {
      this.authService.token$.subscribe(token => {
      this.isLoggedIn = !!token;
    });
  }

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/login']); // Preusmeri na login nakon logout-a
  }
}
