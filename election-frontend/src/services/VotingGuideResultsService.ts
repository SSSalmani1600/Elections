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

export async function saveResults(request: VotingGuideResultResponse) {
  try {
    const res = await fetch(`http://localhost:8080/api/voting-guide/results/save-results`, {
      method: "POST",
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(request)
    })

    if (!res.ok) {
      throw new Error(`Saving answers failed (status ${res.status})`);
    }

  } catch (err: any) {
    throw new Error(`Saving answers error: ${err}`)
  }
}

export async function getResults(): Promise<VotingGuideResultResponse> {
  try {
    const res = await fetch(`http://localhost:8080/api/voting-guide/results/get-results`, {
      method: "GET",
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    })

    if (!res.ok) {
      throw new Error(`Fetching results failed ${res.status}`);
    }

    return await res.json()
  } catch (err: any) {
    throw new Error(`Fetching results error: ${err}`)
  }
}
