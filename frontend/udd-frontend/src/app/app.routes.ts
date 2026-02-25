import { Routes } from '@angular/router';
import { UploadForm } from './upload-form/upload-form';
import { SearchForensicReport } from './search-forensic-report/search-forensic-report';
import { PreviewForensicReport } from './preview-forensic-report/preview-forensic-report';
import { Statistics } from './statistics/statistics';

export const routes: Routes = [
  { path: 'upload-form', component: UploadForm},
  { path: 'search-reports', component: SearchForensicReport},
  { path: 'preview-report/:filename', component: PreviewForensicReport},
  { path: 'statistics', component: Statistics}
];
