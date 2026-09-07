import { RestClient } from "./RestClient";
import type {
  SubscriptionEntitlementDto,
  SubscriptionEntitlementRequest,
} from "../models/SubscriptionEntitlementDto";

export const SubscriptionEntitlementRepository = {
  async list(applicationId: number): Promise<SubscriptionEntitlementDto[]> {
    const response = await RestClient.get<SubscriptionEntitlementDto[]>(
      `/api/applications/${applicationId}/subscription-entitlements`,
    );
    return response.data;
  },

  async create(
    applicationId: number,
    request: SubscriptionEntitlementRequest,
  ): Promise<SubscriptionEntitlementDto> {
    const response = await RestClient.post<SubscriptionEntitlementDto>(
      `/api/applications/${applicationId}/subscription-entitlements`,
      request,
    );
    return response.data;
  },

  async update(
    applicationId: number,
    id: number,
    request: SubscriptionEntitlementRequest,
  ): Promise<SubscriptionEntitlementDto> {
    const response = await RestClient.put<SubscriptionEntitlementDto>(
      `/api/applications/${applicationId}/subscription-entitlements/${id}`,
      request,
    );
    return response.data;
  },

  async remove(applicationId: number, id: number): Promise<void> {
    await RestClient.delete(`/api/applications/${applicationId}/subscription-entitlements/${id}`);
  },
};
