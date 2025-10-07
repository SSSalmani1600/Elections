import type { LoginResponse } from '@/types/api'

export async function login(email: string, password: string): Promise<LoginResponse> {
    const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: email,
            password: password,
        }),
    })

    if (!res.ok) {
        throw new Error(`Login failed (${res.status})`)
    }

    return (await res.json()) as LoginResponse
}
