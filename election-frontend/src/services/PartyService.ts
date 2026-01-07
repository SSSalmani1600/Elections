import { apiFetch, backendAPI } from "@/apiClient"
import type { BasicPartyInfo, PartyDetail } from "@/types/api"

export async function getParties(): Promise<BasicPartyInfo[]> {
  const res = await apiFetch(`${backendAPI}/api/parties`, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
  }, false)

  if (!res.ok) {
    throw new Error(`Failed to retrieve parties: ${res.status}`)
  }

  return (await res.json()) as BasicPartyInfo[]
}

export async function getPartyDetail(partyIdOrName: string): Promise<PartyDetail> {
  const res = await apiFetch(`${backendAPI}/api/parties/${encodeURIComponent(partyIdOrName)}`, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
  }, false)

  if (!res.ok) {
    const msg = await res.text().catch(() => "")
    throw new Error(msg || `Failed to retrieve party detail: ${res.status}`)
  }

  return (await res.json()) as PartyDetail
}
