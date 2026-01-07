import type { BasicPartyInfo } from '@/types/api.ts'
import { backendAPI } from '@/apiClient';

export async function getParties(): Promise<Set<BasicPartyInfo>> {
  const res = await fetch(`${backendAPI}/api/parties`, {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to retrieve parties: ${res.status}`)
  }
  return (await res.json()) as Set<BasicPartyInfo>
}
