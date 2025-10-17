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
export interface Election {
  id: string
  type: string
  date: string
  status: string
}

export async function getUpcomingElections(): Promise<Election[]> {
  const res = await fetch('http://localhost:8080/api/next-elections', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to fetch upcoming elections: ${res.statusText}`)
  }

  return (await res.json()) as Election[]
}
