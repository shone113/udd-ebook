import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-statistics',
  imports: [],
  templateUrl: './statistics.html',
  styleUrl: './statistics.css',
})
export class Statistics {

  kibanaUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {
    const rawUrl = "http://localhost:5601/app/dashboards#/view/f437d3ca-e973-4d31-8b62-399755e38f00?embed=true&_g=%28refreshInterval%3A%28pause%3A%21t%2Cvalue%3A60000%29%2Ctime%3A%28from%3Anow-15m%2Cto%3Anow%29%29&hide-filter-bar=true";
    this.kibanaUrl = this.sanitizer.bypassSecurityTrustResourceUrl(rawUrl);
  }
}
