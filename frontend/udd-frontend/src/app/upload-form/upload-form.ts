import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; // Ovo nam sada treba
import { ForensicReport } from '../models/forensic-report.model';
import { ForensicReportService } from '../services/forensic-report.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upload-form',
  standalone: true,
  imports: [FormsModule, CommonModule], // FormsModule umesto Reactive
  templateUrl: './upload-form.html',
  styleUrl: './upload-form.css',
})
export class UploadForm {
  // Inicijalizujemo prazan objekat koji prati tvoj model
  report: any = {
    analysts: '',
    organizationName: '',
    malwareName: '',
    malwareDescription: '',
    threatClassification: '',
    sampleHash: '',
    address: {
      road: '',
      houseNumber: '',
      city: '',
      country: ''
    }
  };

  selectedFile: File | null = null;
  isUploaded = false;

  constructor(private forensicReportService: ForensicReportService) {}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onUpload() {
    if (this.selectedFile) {
      this.forensicReportService.uploadFile(this.selectedFile).subscribe({
        next: (data) => {
          this.isUploaded = true;
          console.log("Podaci: ", data);
          // Mapiramo direktno na naš objekat
          this.report = {
            ...data,
            // Ako je niz, spoji ga u string za lakšu izmenu u inputu
            analysts: Array.isArray(data.analysts) ? data.analysts.join(', ') : data.analysts
          };
        },
        error: (err) => console.error('Greška pri uploadu', err)
      });
    }
  }

  onIndexDocument() {
    // Šaljemo direktno "report" objekat
    console.log('Šaljem na indeksiranje:', this.report);

    const analystsString = this.report.analysts; // "Marko Petrovic, Ivana Milosavljević"

    // Podeli po zarezu i očisti praznine
    const analystsArray = analystsString.split(',').map((s: string) => s.trim());

    // Kreiraj novi objekat sa analysts kao nizom
    const reportToSend = {
        ...this.report,
        analysts: analystsArray  // Sada je ["Marko Petrovic", "Ivana Milosavljević"]
    };

    this.forensicReportService.indexForensicReport(reportToSend).subscribe({
      next: () => alert('Indeksirano!'),
      error: (err) => console.error('Greška:', err)
    });
  }
}
