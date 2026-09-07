import { RestClient } from "./RestClient";
import type { SubscriptionPlanDto, SubscriptionPlanRequest } from "../models/SubscriptionPlanDto";
import type { ReplaceSubscriptionPlanGrantsRequest } from "../models/SubscriptionPlanGrantDto";

export const SubscriptionPlanRepository = {
  async list(applicationId: number): Promise<SubscriptionPlanDto[]> {
    const response = await RestClient.get<SubscriptionPlanDto[]>(
      `/api/applications/${applicationId}/subscription-plans`,
    );
    return response.data;
  },

  async create(applicationId: number, request: SubscriptionPlanRequest): Promise<SubscriptionPlanDto> {
    const response = await RestClient.post<SubscriptionPlanDto>(
      `/api/applications/${applicationId}/subscription-plans`,
      request,
    );
    return response.data;
  },

  async update(
    applicationId: number,
    id: number,
    request: SubscriptionPlanRequest,
  ): Promise<SubscriptionPlanDto> {
    const response = await RestClient.put<SubscriptionPlanDto>(
      `/api/applications/${applicationId}/subscription-plans/${id}`,
      request,
    );
    return response.data;
  },

  async remove(applicationId: number, id: number): Promise<void> {
    await RestClient.delete(`/api/applications/${applicationId}/subscription-plans/${id}`);
  },

  async getVersions(applicationId: number, id: number): Promise<SubscriptionPlanDto[]> {
    const response = await RestClient.get<SubscriptionPlanDto[]>(
      `/api/applications/${applicationId}/subscription-plans/${id}/versions`,
    );
    return response.data;
  },

  async replaceGrants(
    applicationId: number,
    id: number,
    request: ReplaceSubscriptionPlanGrantsRequest,
  ): Promise<SubscriptionPlanDto> {
    const response = await RestClient.put<SubscriptionPlanDto>(
      `/api/applications/${applicationId}/subscription-plans/${id}/grants`,
      request,
    );
    return response.data;
  },
};
