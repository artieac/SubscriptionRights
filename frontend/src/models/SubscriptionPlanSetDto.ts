export interface SubscriptionPlanSetItemDto {
  subscriptionPlanId: number;
  subscriptionPlanVersion: number;
  tier: number;
}

export interface SubscriptionPlanSetDto {
  id: number;
  applicationId: number;
  name: string;
  effectiveStartDate: string;
  effectiveEndDate: string | null;
  items: SubscriptionPlanSetItemDto[];
  createdAt: string;
  updatedAt: string;
}

export interface SubscriptionPlanSetItemRequest {
  subscriptionPlanId: number;
  subscriptionPlanVersion: number;
  tier: number;
}

export interface SubscriptionPlanSetRequest {
  name: string;
  effectiveStartDate: string;
  effectiveEndDate: string | null;
  items: SubscriptionPlanSetItemRequest[];
}
