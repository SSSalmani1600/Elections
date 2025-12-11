import type { VotingGuideResultRequest, VotingGuideResultResponse } from '@/types/api.ts'

export async function calculateResults(request: VotingGuideResultRequest): Promise<VotingGuideResultResponse> {
  const res = await fetch(`http://localhost:8080/api/voting-guide/results/calculate`, {
    method: "POST",
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request)
  })

  if (!res.ok) {
    throw new Error(`Calculating the results failed, ${res.status}`)
  }

  return await res.json()
}
