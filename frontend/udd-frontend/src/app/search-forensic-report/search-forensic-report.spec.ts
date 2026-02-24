import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchForensicReport } from './search-forensic-report';

describe('SearchForensicReport', () => {
  let component: SearchForensicReport;
  let fixture: ComponentFixture<SearchForensicReport>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchForensicReport]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchForensicReport);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
