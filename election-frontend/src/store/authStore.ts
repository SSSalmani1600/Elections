import { apiFetch } from '@/apiClient'
import { loginRequest, logoutRequest } from '@/services/AuthService'
import type { User } from '@/types/api'
import { reactive, readonly, toRefs } from 'vue'

type AuthState = {
  user: User | null
  loading: boolean
  initialized: boolean
}

const state = reactive<AuthState>({
  user: null,
  loading: false,
  initialized: false,
})

async function fetchUser() {
  state.loading = true
  try {
    const res = await apiFetch('http://localhost:8080/api/auth/session')

    if (res.ok) {
      const data = await res.json()
      state.user = data
    } else {
      state.user = null
    }
  } catch (e) {
    console.log(e)
    state.user = null
  } finally {
    state.loading = false
    state.initialized = true
  }
}

async function initAuth() {
  if (state.initialized) return
  await fetchUser()
}

async function login(email: string, password: string) {
  state.loading = true
  try {
    const user = await loginRequest(email, password)
    state.user = user
  } finally {
    state.loading = false
    state.initialized = true
  }
}

async function logout() {
  await logoutRequest()
  state.user = null
}

export const authStore = {
  state: readonly(state),
  initAuth,
  login,
  logout,
}

export function useAuth() {
  const { user, loading, initialized } = toRefs(state)

  return {
    user,
    loading,
    initialized,
    login,
    logout,
  }
}
