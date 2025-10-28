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
export interface Election {
  id: string
  type: string
  date: string
  status: string
}

export async function getUpcomingElections(): Promise<Election[]> {
  const token = localStorage.getItem('token')

  const res = await fetch('http://localhost:8080/api/next-elections', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`, // ✅ belangrijk
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to fetch upcoming elections: ${res.statusText}`)
  }

  return (await res.json()) as Election[]
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
