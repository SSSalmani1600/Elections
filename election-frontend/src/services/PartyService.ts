import { apiFetch } from "@/apiClient"
import type { Party2 } from "@/types/api"

export async function getParties(): Promise<Party2[]> {
  const res = await apiFetch("/api/parties")

  if (!res.ok) {
    throw new Error("Kon partijen niet ophalen")
  }

  return await res.json()
}
