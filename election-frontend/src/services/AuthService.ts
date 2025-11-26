import { apiFetch } from '@/apiClient'
import type { LoginResponse, RegisterResponse } from '@/types/api'

export async function loginRequest(email: string, password: string): Promise<LoginResponse> {
  const res = await apiFetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      email,
      password,
    }),
  })

  if (!res.ok) {
    throw new Error(`Login failed (${res.status})`)
  }

  const data = (await res.json()) as LoginResponse

  return data
}

export async function logoutRequest(): Promise<void> {
  await apiFetch(
    'http://localhost:8080/api/auth/login/logout',
    {
      method: 'POST',
    },
    false,
  )
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
      email: email,
      password: password,
      username: username,
    }),
  })

  if (!res.ok) throw new Error(await res.text())

  return (await res.json()) as RegisterResponse
}
