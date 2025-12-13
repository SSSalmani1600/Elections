import { apiFetch } from "@/apiClient"
import type { PartyDetail, Party2 } from "@/types/api"

export async function getParties(): Promise<Party2[]> {
  const res = await apiFetch("/api/parties")
  if (!res.ok) throw new Error("Kon partijen niet ophalen")
  return await res.json()
}

export async function getPartyDetail(id: string): Promise<PartyDetail> {
  const res = await apiFetch(`/api/parties/${id}`)
  if (!res.ok) throw new Error("Kon partijdetail niet ophalen")
  return await res.json()
}
