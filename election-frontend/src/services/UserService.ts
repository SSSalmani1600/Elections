import type {User} from "@/types/api.ts";

export async function getAllUsers(): Promise<User[]> {
  const res = await fetch(`http://localhost:8080/api/users/all`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to fetch parties: ${res.statusText}`)
  }

  return (await res.json()) as User[];

}
