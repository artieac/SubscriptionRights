export interface SubscriptionPlanDto {
  id: number;
  applicationId: number;
  name: string;
  description: string | null;
  version: number;
  createdAt: string;
}

export interface SubscriptionPlanRequest {
  name: string;
  description: string | null;
}
