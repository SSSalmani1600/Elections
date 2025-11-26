import type {Statement} from "@/types/api.ts";

export async function getAllStatements(): Promise<Set<Statement>> {
  try {
    const res = await fetch("http://localhost:8080/api/statements/all")

    if (!res.ok) {
      const text = await res.text()
      throw new Error(`Backend error ${res.status}: ${text}`)
    }

    return await res.json()
  } catch {
    throw new Error("Cannot get a connection with the server")
  }
}
