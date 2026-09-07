export interface ApiTokenDto {
  id: number;
  applicationId: number;
  name: string;
  tokenPrefix: string;
  createdAt: string;
  revokedAt: string | null;
}

export interface IssuedApiTokenDto extends ApiTokenDto {
  rawToken: string;
}

export interface ApiTokenRequest {
  name: string;
}
