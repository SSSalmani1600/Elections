import type {AffiliationResponse, LoginResponse} from "@/types/api.ts";

export async function getAffiliations(): Promise<Set<String>> {
  const res = await fetch("http://localhost:8081/elections/TK2023/affiliations", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to retrieve affiliations: ${res.status}`)
  }

  return (await res.json()) as Set<String>
}
