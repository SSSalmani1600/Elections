import type { ParserResponse } from '@/types/api'

export async function getParties(electionYear: number): Promise<ParserResponse> {
  const res = await fetch(`http://localhost:8080/elections/TK${electionYear.toString()}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to fetch parties: ${res.statusText}`)
  }

  return (await res.json()) as ParserResponse
}
