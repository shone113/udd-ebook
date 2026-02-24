import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { DynamicSummary } from '../models/dynamic-summary.model';
import { ForensicReportService } from '../services/forensic-report.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-search-forensic-report',
  imports: [
    CommonModule,
    MatTabsModule,
    RouterModule
  ],
  templateUrl: './search-forensic-report.html',
  styleUrl: './search-forensic-report.css',
})
export class SearchForensicReport {

  activeTab: string = 'opsta';
  results: DynamicSummary[] = []

  constructor(private forensicReportService: ForensicReportService){}

  generalDataSearch(analyst: String, hash: String, classification: String){
    this.forensicReportService.generalDataSearch(analyst, hash, classification).subscribe({
      next: (data) => {
        this.results = data;
      },
      error: (err) => console.log("Greska pri pretrazivanju generalnih podataka")
    })
  }

  organizationSearch(organizationInput: String, malwareInput: String){
    this.forensicReportService.organizationSearch(organizationInput, malwareInput).subscribe({
      next: (data) => {
        this.results = data;
      },
      error: (err) => console.log("Greska za pretragu po imenu organizacije")
    })
  }

  booleanSearch(booleanInput: String){
    this.forensicReportService.booleanSearch(booleanInput).subscribe({
      next: (data) => {
        this.results = data;
      },
      error: (err) => console.log("Greska za boolean pretragu")
    })
  }

  fullTextSearch(inputContent: String){
    console.log("Desava se nesto");
    this.forensicReportService.fullTextSearch(inputContent).subscribe({
      next: (data) => {
        this.results = data;
      },
      error: (err) => console.log("Greska za full text query")
    })
  }
}
