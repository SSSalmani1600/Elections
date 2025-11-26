import type { LoginResponse, RegisterResponse } from '@/types/api'

export async function login(email: string, password: string): Promise<LoginResponse> {
  const res = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password }),
  })

  if (!res.ok) {
    throw new Error(`Login failed (${res.status})`)
  }

  const data = (await res.json()) as LoginResponse


  localStorage.setItem('JWT', data.token)
  localStorage.setItem('userId', String(data.id))
  localStorage.setItem('username', data.displayName)
  localStorage.setItem('isAdmin', String(data.isAdmin))

  return data
}

export async function register(email: string, password: string, username: string): Promise<RegisterResponse> {
  const res = await fetch("http://localhost:8080/api/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      email,
      password,
      displayName: username,
    }),
  })

  if (!res.ok) throw new Error(await res.text())

  return (await res.json()) as RegisterResponse;
}
