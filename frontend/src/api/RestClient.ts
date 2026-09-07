import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const RestClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

export function backendUrl(path: string): string {
  return `${API_BASE_URL}${path}`;
}
