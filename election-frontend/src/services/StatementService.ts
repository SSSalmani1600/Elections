import type {Statement} from "@/types/api.ts";

export async function getAllStatements(): Promise<Set<Statement>> {
  const res: Response = await fetch("http://localhost:8080/api/statements/all", {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to retrieve statements: ${res.status}`)
  }

  return (await res.json() as Set<Statement>)
}
