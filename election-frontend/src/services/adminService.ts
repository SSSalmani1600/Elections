import axios from "axios";
import type { AdminStats } from "@/types/api";

const API_URL = "http://localhost:8080/admin";

export async function getAdminStats(): Promise<AdminStats> {
  const response = await axios.get<AdminStats>(`${API_URL}/stats`);
  return response.data;
}
