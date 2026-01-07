import { backendAPI } from '@/apiClient';

export async function getAdminStats() {
  const token = localStorage.getItem("JWT");

  const res = await fetch(`${backendAPI}/api/admin/stats`, {
    method: "GET",
    headers: {
      "Authorization": "Bearer " + token,
      "Content-Type": "application/json",
    }
  });

  if (!res.ok) {
    console.error("Admin error:", await res.text());
    throw new Error("Not allowed");
  }

  return await res.json();
}
