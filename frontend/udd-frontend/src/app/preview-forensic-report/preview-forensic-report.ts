import { Component } from '@angular/core';
import { ForensicReportService } from '../services/forensic-report.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-preview-forensic-report',
  imports: [
    CommonModule
  ],
  templateUrl: './preview-forensic-report.html',
  styleUrl: './preview-forensic-report.css',
})
export class PreviewForensicReport {

  filename = 'neki-fajl.pdf'; // ili iz inputa/parametra
  safeUrl: SafeResourceUrl | null = null;

  constructor(
    private forensicReportService: ForensicReportService,
    private sanitizer: DomSanitizer,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const filename = this.route.snapshot.paramMap.get('filename');
    if (filename) {
      const url = `http://localhost:8080/api/file/${filename}`;
      // Označavamo URL kao bezbedan za iframe
      this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
  }

}
