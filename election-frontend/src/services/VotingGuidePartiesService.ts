import type { VotingGuideParty } from '@/types/api.ts'
import { apiFetch } from '@/apiClient'

export async function getAllVotingGuideParties(): Promise<VotingGuideParty[]> {
  try {
    const res = await apiFetch("/api/voting-guide/parties/all", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    })

    if (!res.ok) {
      throw new Error(`Something went wrong with fetching the voting guide parties - ${res.status}`)
    }

    return await res.json()
  } catch (err: any) {
    throw new Error(err.message)
  }
}
