import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewForensicReport } from './preview-forensic-report';

describe('PreviewForensicReport', () => {
  let component: PreviewForensicReport;
  let fixture: ComponentFixture<PreviewForensicReport>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreviewForensicReport]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewForensicReport);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
