import type {AffiliationResponse, LoginResponse} from "@/types/api.ts";

export async function getAffiliations(): Promise<AffiliationResponse> {
  const res = await fetch("http://localhost:8081/elections/TK2023?folderName=TK2023-Partial", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to retrieve affiliations: ${res.status}`)
  }

  return (await res.json()) as AffiliationResponse
}
