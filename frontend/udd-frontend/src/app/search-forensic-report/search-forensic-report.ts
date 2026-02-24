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
  isLoading = false;

  constructor(private forensicReportService: ForensicReportService){}

  generalDataSearch(analyst: String, hash: String, classification: String){
    this.isLoading = true;

    this.forensicReportService.generalDataSearch(analyst, hash, classification).subscribe({
      next: (data) => {
        this.results = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.log("Greska pri pretrazivanju generalnih podataka")
        this.isLoading = false;
      }
    })
  }

  organizationSearch(organizationInput: String, malwareInput: String){
    this.isLoading = true;

    this.forensicReportService.organizationSearch(organizationInput, malwareInput).subscribe({
      next: (data) => {
        this.results = data;
        this.isLoading = false;
      },
      error: (err) =>{
         console.log("Greska za pretragu po imenu organizacije")
         this.isLoading = false;
      }
    })
  }

  booleanSearch(booleanInput: String){
    this.isLoading = true;

    this.forensicReportService.booleanSearch(booleanInput).subscribe({
      next: (data) => {
        this.results = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.log("Greska za boolean pretragu")
        this.isLoading = false;
      }
    })
  }

  fullTextSearch(inputContent: String){
    this.isLoading = true;

    console.log("Desava se nesto");
    this.forensicReportService.fullTextSearch(inputContent).subscribe({
      next: (data) => {
        this.results = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.log("Greska za full text query")
        this.isLoading = false;
      }
    })
  }

  locationSearch(address: String, city: String, radius: String){
     this.isLoading = true;

    console.log("Desava se nesto");
    this.forensicReportService.searchByLocation(address, city, radius).subscribe({
      next: (data) => {
        this.results = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.log("Greska za pretragu po lokaciji")
        this.isLoading = false;
      }
    })
  }

}
