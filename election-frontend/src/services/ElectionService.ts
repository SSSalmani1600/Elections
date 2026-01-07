import { apiFetch, backendAPI } from '@/apiClient'
import type { Constituency, Municipality } from '@/types/api'

export interface Election {
  id: string
  type: string
  date: string
  status: string
}

export async function getUpcomingElections(): Promise<Election[]> {
  const res = await apiFetch(
    `${backendAPI}/api/electionresults/next-elections`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    },
    false,
  )

  if (!res.ok) {
    throw new Error(`Failed to fetch upcoming elections: ${res.statusText}`)
  }

  return (await res.json()) as Election[]
}

export async function getConstituencies(electionId: string): Promise<Constituency[]> {
  const res = await apiFetch(
    `${backendAPI}/api/electionresults/${electionId}/constituencies`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    },
    false,
  )

  if (!res.ok) {
    throw new Error('Failed to fetch constituencies')
  }

  return (await res.json()) as Constituency[]
}

export async function getMunicipalities(electionId: string): Promise<string[]> {
  const res = await apiFetch(
    `http://localhost:8080/api/electionresults/${electionId}/municipalities`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    },
    false,
  )

  if (!res.ok) {
    throw new Error('Failed to fetch municipality')
  }

  return (await res.json()) as string[]
}

export async function getMunicipalityData(electionId: string, name: string): Promise<Municipality> {
  const res = await apiFetch(
    `${backendAPI}/api/electionresults/${electionId}/municipalities/${name}`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    },
    false,
  )

  if (!res.ok) {
    throw new Error('Failed to fetch municipality')
  }

  return (await res.json()) as Municipality
}

export async function getElectionYears(): Promise<number[]> {
  const res = await apiFetch(
    `${backendAPI}/api/electionresults`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    },
    false,
  )

  const years: number[] = await res.json()

  if (years) return years

  return []
}
