import type { Constituency, ParserResponse } from '@/types/api'

export async function getParties(): Promise<ParserResponse> {
  const res = await fetch(`http://localhost:8080/api/parties`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to fetch parties: ${res.statusText}`)
  }

  return (await res.json()) as ParserResponse
}

export async function getConstituencies(): Promise<Constituency[]> {
  const res = await fetch("http://localhost:8080/api/elections/constituencies", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })

  if (!res.ok) {
    throw new Error("Failed to fetch constituencies");
  }

  return (await res.json()) as Constituency[];
}
