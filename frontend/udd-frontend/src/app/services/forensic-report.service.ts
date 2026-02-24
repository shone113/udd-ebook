import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { AuthService } from "./auth.service";
import { Observable } from "rxjs";
import { ForensicReport } from "../models/forensic-report.model";
import { DynamicSummary } from "../models/dynamic-summary.model";

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
    return this.http.post<ForensicReport>(`http://localhost:8080/api/index/confirm`, forensicReport);
  }

  public fullTextSearch(query: String) : Observable<DynamicSummary[]>{
    return this.http.get<DynamicSummary[]>(`http://localhost:8080/api/search/full-text?query=${query}`);
  }

  public booleanSearch(query: String) : Observable<DynamicSummary[]>{
    return this.http.get<DynamicSummary[]>(`http://localhost:8080/api/search/boolean?query=${query}`)
  }

  public generalDataSearch(analyst: String, hash: String, classification: String) : Observable<DynamicSummary[]>{
    return this.http.get<DynamicSummary[]>(`http://localhost:8080/api/search/general-data?analyst=${analyst}&hash=${hash}&classification=${classification}`);
  }

  public organizationSearch(organization: String, malware: String) : Observable<DynamicSummary[]>{
    return this.http.get<DynamicSummary[]>(`http://localhost:8080/api/search/org-and-malware?organization=${organization}&malware=${malware}`);
  }

  public getFileUrl(filename: string): string {
    return `http://localhost:8080/api/files/${filename}`;
  }

}
