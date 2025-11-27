import type {User} from "@/types/api.ts";

// Service voor gebruikers-gerelateerde API calls
// Deze functies communiceren met de backend om gebruikersdata op te halen en te wijzigen

// Haalt alle gebruikers op (waarschijnlijk alleen voor admin)
export async function getAllUsers(): Promise<User[]> {
  const res = await fetch(`http://localhost:8080/api/users/all`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })

  if (!res.ok) {
    throw new Error(`Failed to fetch users: ${res.statusText}`)
  }

  return (await res.json()) as User[];
}

// Haalt de huidige ingelogde gebruiker op via JWT token
// Gebruikt door de account pagina om gebruikersgegevens te tonen
export async function getCurrentUser(): Promise<User> {
  const token = localStorage.getItem('JWT')
  if (!token) {
    throw new Error('No authentication token found')
  }

  const res = await fetch(`http://localhost:8080/api/users/me`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  })

  if (!res.ok) {
    if (res.status === 401) {
      throw new Error('Unauthorized: Invalid or expired token')
    }
    throw new Error(`Failed to fetch current user (${res.status}): ${res.statusText}`)
  }

  return (await res.json()) as User;
}

// Haalt een specifieke gebruiker op op basis van ID
export async function getUserById(id: number): Promise<User> {
  const token = localStorage.getItem('JWT')
  const res = await fetch(`http://localhost:8080/api/users/${id}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
    },
  })

  if (!res.ok) {
    if (res.status === 404) {
      throw new Error(`User not found (404): User with ID ${id} does not exist`)
    }
    throw new Error(`Failed to fetch user (${res.status}): ${res.statusText}`)
  }

  return (await res.json()) as User;
}

// Interface voor het updaten van gebruikersgegevens
export interface UpdateUserRequest {
  currentPassword?: string;  // Verplicht: huidig wachtwoord voor verificatie
  username?: string;         // Optioneel: nieuwe gebruikersnaam
  email?: string;           // Optioneel: nieuw email adres
  password?: string;        // Optioneel: nieuw wachtwoord
}

// Werkt gebruikersgegevens bij (username, email, wachtwoord)
// Gebruikt door de account pagina wanneer gebruiker zijn gegevens wil wijzigen
export async function updateUser(id: number, updates: UpdateUserRequest): Promise<User> {
  const token = localStorage.getItem('JWT')
  const res = await fetch(`http://localhost:8080/api/users/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
    },
    body: JSON.stringify(updates),
  })

  if (!res.ok) {
    const errorText = await res.text()
    throw new Error(errorText || `Failed to update user: ${res.statusText}`)
  }

  return (await res.json()) as User;
}
