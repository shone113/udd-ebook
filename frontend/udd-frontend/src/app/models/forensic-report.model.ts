
export interface ForensicReport {
  analysts: string[];
  organizationName: string;
  malwareName: string;
  malwareDescription: string;
  threatClassification: string;
  sampleHash: string;
  road: string;
  houseNumber: string;
  city: string;
  fileName: string;
  serverFilename: string;
  title: string;
  databaseId: number;
  content: string;
}
