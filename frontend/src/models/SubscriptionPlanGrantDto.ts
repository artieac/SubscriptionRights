export interface SubscriptionPlanGrantDto {
  id: number;
  applicationId: number;
  subscriptionPlanId: number;
  subscriptionPlanVersion: number;
  subscriptionEntitlementId: number;
  value: number;
  createdAt: string;
}

export interface SubscriptionPlanGrantRequest {
  subscriptionPlanId: number;
  subscriptionEntitlementId: number;
  value: number;
}

export interface SubscriptionPlanGrantItemRequest {
  subscriptionEntitlementId: number;
  value: number;
}

export interface ReplaceSubscriptionPlanGrantsRequest {
  items: SubscriptionPlanGrantItemRequest[];
  createNewVersion: boolean;
  targetVersion: number;
}
