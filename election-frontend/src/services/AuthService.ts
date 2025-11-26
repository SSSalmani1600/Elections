import type { LoginResponse, RegisterResponse } from '@/types/api'

export async function login(email: string, password: string): Promise<LoginResponse> {
  const res = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password }),
    credentials: 'include', // ⭐ nodig voor cookies (main)
  })

  if (!res.ok) {
    throw new Error(`Login failed (${res.status})`)
  }

  const data = (await res.json()) as LoginResponse

  // ⭐ Token opslaan als de backend hem meestuurt
  if (data.token) {
    localStorage.setItem('JWT', data.token)
  }

  // ⭐ Jouw extra informatie opslaan
  localStorage.setItem('userId', String(data.id))
  localStorage.setItem('username', data.displayName)
  localStorage.setItem('isAdmin', String(data.isAdmin))

  return data
}

export async function register(
  email: string,
  password: string,
  username: string,
): Promise<RegisterResponse> {
  const res = await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      email,
      password,
      username, // ⭐ main gebruikt "username" — correct
    }),
  })

  if (!res.ok) throw new Error(await res.text())

  return (await res.json()) as RegisterResponse
}
