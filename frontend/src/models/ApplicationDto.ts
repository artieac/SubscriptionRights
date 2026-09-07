export interface ApplicationDto {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
}

export interface ApplicationRequest {
  name: string;
  description: string | null;
}
