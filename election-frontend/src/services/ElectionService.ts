import type { ParserResponse } from '@/types/api'

export async function getParties(electionYear: number): Promise<ParserResponse> {
  const res = await fetch(`http://localhost:8081/elections/TK${electionYear.toString()}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  return (await res.json()) as ParserResponse
}
