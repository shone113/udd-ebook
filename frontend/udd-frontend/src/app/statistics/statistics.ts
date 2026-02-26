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
    const rawUrl = "http://localhost:5601/app/r/s/KGU0F";
    this.kibanaUrl = this.sanitizer.bypassSecurityTrustResourceUrl(rawUrl);
  }
}
