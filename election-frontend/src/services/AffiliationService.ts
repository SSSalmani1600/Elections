export async function getAffiliations(): Promise<Set<String>> {
  const res = await fetch("http://localhost:8080/api/affiliations/names", {
    method: "GET",
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to retrieve affiliations: ${res.status}`)
  }
  return (await res.json()) as Set<String>
}
