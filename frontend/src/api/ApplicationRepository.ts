import { RestClient } from "./RestClient";
import type { ApplicationDto, ApplicationRequest } from "../models/ApplicationDto";

export const ApplicationRepository = {
  async list(): Promise<ApplicationDto[]> {
    const response = await RestClient.get<ApplicationDto[]>("/api/applications");
    return response.data;
  },

  async getById(id: number): Promise<ApplicationDto> {
    const response = await RestClient.get<ApplicationDto>(`/api/applications/${id}`);
    return response.data;
  },

  async create(request: ApplicationRequest): Promise<ApplicationDto> {
    const response = await RestClient.post<ApplicationDto>("/api/applications", request);
    return response.data;
  },

  async update(id: number, request: ApplicationRequest): Promise<ApplicationDto> {
    const response = await RestClient.put<ApplicationDto>(`/api/applications/${id}`, request);
    return response.data;
  },

  async remove(id: number): Promise<void> {
    await RestClient.delete(`/api/applications/${id}`);
  },
};
