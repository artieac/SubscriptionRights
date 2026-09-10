export interface ApplicationDto {
  id: number;
  name: string;
  externalId: string;
  description: string | null;
  createdAt: string;
}

export interface ApplicationRequest {
  name: string;
  externalId: string;
  description: string | null;
}
