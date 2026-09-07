import { RestClient } from "./RestClient";
import type { SubscriptionPlanSetDto, SubscriptionPlanSetRequest } from "../models/SubscriptionPlanSetDto";

export const SubscriptionPlanSetRepository = {
  async list(applicationId: number): Promise<SubscriptionPlanSetDto[]> {
    const response = await RestClient.get<SubscriptionPlanSetDto[]>(
      `/api/applications/${applicationId}/subscription-plan-sets`,
    );
    return response.data;
  },

  async getById(applicationId: number, id: number): Promise<SubscriptionPlanSetDto> {
    const response = await RestClient.get<SubscriptionPlanSetDto>(
      `/api/applications/${applicationId}/subscription-plan-sets/${id}`,
    );
    return response.data;
  },

  async create(
    applicationId: number,
    request: SubscriptionPlanSetRequest,
  ): Promise<SubscriptionPlanSetDto> {
    const response = await RestClient.post<SubscriptionPlanSetDto>(
      `/api/applications/${applicationId}/subscription-plan-sets`,
      request,
    );
    return response.data;
  },

  async update(
    applicationId: number,
    id: number,
    request: SubscriptionPlanSetRequest,
  ): Promise<SubscriptionPlanSetDto> {
    const response = await RestClient.put<SubscriptionPlanSetDto>(
      `/api/applications/${applicationId}/subscription-plan-sets/${id}`,
      request,
    );
    return response.data;
  },

  async remove(applicationId: number, id: number): Promise<void> {
    await RestClient.delete(`/api/applications/${applicationId}/subscription-plan-sets/${id}`);
  },

  async getActiveOnDate(applicationId: number, date: string): Promise<SubscriptionPlanSetDto | null> {
    try {
      const response = await RestClient.get<SubscriptionPlanSetDto>(
        `/api/applications/${applicationId}/subscription-plan-sets/active`,
        { params: { date } },
      );
      return response.data;
    } catch (error: unknown) {
      if (isNotFound(error)) {
        return null;
      }
      throw error;
    }
  },
};

function isNotFound(error: unknown): boolean {
  return (
    typeof error === "object" &&
    error !== null &&
    "response" in error &&
    (error as { response?: { status?: number } }).response?.status === 404
  );
}
