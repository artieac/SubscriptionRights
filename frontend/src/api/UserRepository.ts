import { RestClient } from "./RestClient";
import type { UserDto } from "../models/UserDto";

export const UserRepository = {
  async list(): Promise<UserDto[]> {
    const response = await RestClient.get<UserDto[]>("/api/users");
    return response.data;
  },

  async promote(id: number): Promise<UserDto> {
    const response = await RestClient.post<UserDto>(`/api/users/${id}/promote`);
    return response.data;
  },

  async remove(id: number): Promise<void> {
    await RestClient.delete(`/api/users/${id}`);
  },
};
