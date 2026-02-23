import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { AuthService } from "./auth.service";
import { Observable } from "rxjs";
import { ForensicReport } from "../models/forensic-report.model";

@Injectable({
  providedIn: 'root'
})
export class ForensicReportService {


  constructor(private http: HttpClient, private authService: AuthService) { }

  public uploadFile(file: File) : Observable<ForensicReport>{
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ForensicReport>(`http://localhost:8080/api/index`, formData);
  }

  public indexForensicReport(forensicReport: ForensicReport) : Observable<ForensicReport>{


     console.log('PRE SLANJA - forensicReport:', forensicReport);
  console.log('PRE SLANJA - analysts tip:', typeof forensicReport.analysts);
  console.log('PRE SLANJA - analysts vrednost:', forensicReport.analysts);
  console.log('PRE SLANJA - JSON:', JSON.stringify(forensicReport));

    return this.http.post<ForensicReport>(`http://localhost:8080/api/index/confirm`, forensicReport);
  }

}
