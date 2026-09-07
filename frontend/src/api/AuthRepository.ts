import { RestClient, backendUrl } from "./RestClient";
import type { CurrentUserDto } from "../models/UserDto";

export const AuthRepository = {
  loginUrl(): string {
    return backendUrl("/api/auth/login");
  },

  async getCurrentUser(): Promise<CurrentUserDto | null> {
    try {
      const response = await RestClient.get<CurrentUserDto>("/api/auth/me");
      return response.data;
    } catch (error) {
      if (axiosIsUnauthorized(error)) {
        return null;
      }
      throw error;
    }
  },

  async logout(): Promise<void> {
    await RestClient.post("/api/auth/logout");
  },
};

function axiosIsUnauthorized(error: unknown): boolean {
  return (
    typeof error === "object" &&
    error !== null &&
    "response" in error &&
    (error as { response?: { status?: number } }).response?.status === 401
  );
}
