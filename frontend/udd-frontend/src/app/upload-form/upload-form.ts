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

  alert = {
    visible: false,
    title: '',
    message: '',
    isError: false
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
    // Podeli po zarezu i očisti praznine
    const analystsString = this.report.analysts;
    const analystsArray = analystsString.split(',').map((s: string) => s.trim());

    // Kreiraj novi objekat sa analysts kao nizom
    const reportToSend = {
        ...this.report,
        analysts: analystsArray
    };

    this.forensicReportService.indexForensicReport(reportToSend).subscribe({
      next: () => {
        this.report  = {
          analysts: '',
          organizationName: '',
          malwareName: '',
          malwareDescription: '',
          threatClassification: '',
          sampleHash: '',
          road: '',
          houseNumber: '',
          city: '',
          country: ''
        }
          this.showAlert('Uspeh!', 'Dokument je uspešno indeksiran.', false);
      },
      error: (err) => console.error('Greška:', err)
    });
  }

  showAlert(title: string, message: string, isError: boolean = false) {
    this.alert = { visible: true, title, message, isError };

    // Automatsko zatvaranje nakon 5 sekundi
    setTimeout(() => {
      this.alert.visible = false;
    }, 5000);
  }
}
