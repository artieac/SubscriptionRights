import { RestClient } from "./RestClient";
import type { ApiTokenDto, ApiTokenRequest, IssuedApiTokenDto } from "../models/ApiTokenDto";

export const ApiTokenRepository = {
  async list(applicationId: number): Promise<ApiTokenDto[]> {
    const response = await RestClient.get<ApiTokenDto[]>(`/api/applications/${applicationId}/api-tokens`);
    return response.data;
  },

  async create(applicationId: number, request: ApiTokenRequest): Promise<IssuedApiTokenDto> {
    const response = await RestClient.post<IssuedApiTokenDto>(
      `/api/applications/${applicationId}/api-tokens`,
      request,
    );
    return response.data;
  },

  async revoke(applicationId: number, id: number): Promise<void> {
    await RestClient.delete(`/api/applications/${applicationId}/api-tokens/${id}`);
  },
};
