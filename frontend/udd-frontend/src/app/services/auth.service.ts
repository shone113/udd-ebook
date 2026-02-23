import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Router } from "@angular/router";
import { LoginDetails } from "../models/login-details.model";
import { Observable, BehaviorSubject } from "rxjs"
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private tokenSubject = new BehaviorSubject<string>('');
  public token$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {
    this.refreshToken();
  }

  // public login(loginDetails: LoginDetails) : Observable<string> {
  //   return this.http.post(`${enivironment.backenUrl}/merchant/login`, loginDetails, {responseType: 'text'});
  // }

  // public verifyCode(verificationCode: VerificationCode) : Observable<string> {
  //   return this.http.post(`${enivironment.backenUrl}/merchant/verify-mfa`, verificationCode, {responseType: 'text'});
  // }

  public getToken() : string {
    return localStorage.getItem("psp_token") || "";
  }

    getDecodedToken(): JwtPayload | null {
    const token = localStorage.getItem('psp_token');
    if (!token) return null;

    try {
      return jwtDecode<JwtPayload>(token);
    } catch (e) {

      return null;
    }
  }

  public getEmail(): string {
    const decoded = this.getDecodedToken();

    if (decoded) {
      const email = decoded.sub;
      return email;
    }
    return ""
  }

  public getRole(): string {
    const decoded = this.getDecodedToken();
    // Vratiće npr. 'ADMIN' ili 'MERCHANT'
    return decoded?.role || "";
  }

  public isAdmin(): boolean {
    return this.getRole() === 'ROLE_ADMIN';
  }


  public isSuperAdmin(): boolean {
    return this.getRole() === 'ROLE_SUPERADMIN';
  }

  public getHeaderToken() : HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.getToken()}`
    });
  }

  public logout() {
    localStorage.removeItem("psp_token");
    this.refreshToken();
  }

  public refreshToken() {
    this.tokenSubject.next(this.getToken())
  }
}

interface JwtPayload {
  sub: string;
  role?: string;
  exp?: number;
  sid?: string;
}
