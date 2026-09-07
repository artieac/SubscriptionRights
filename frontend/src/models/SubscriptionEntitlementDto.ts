export interface SubscriptionEntitlementDto {
  id: number;
  applicationId: number;
  name: string;
  displayName: string;
  createdAt: string;
  updatedAt: string;
}

export interface SubscriptionEntitlementRequest {
  name: string;
  displayName: string;
}
