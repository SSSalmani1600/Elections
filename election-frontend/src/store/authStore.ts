/**
 * authStore.ts - Centrale authenticatie state management
 * 
 * Dit bestand beheert de login-status van de gebruiker in de hele applicatie.
 * Wordt gebruikt door AccountView om te checken of gebruiker ingelogd is.
 */

import { apiFetch } from '@/apiClient'
import { loginRequest, logoutRequest } from '@/services/AuthService'
import type { User } from '@/types/api'
import { reactive, readonly, toRefs } from 'vue'

// Type definitie voor de authenticatie state
type AuthState = {
  user: User | null      // Ingelogde gebruiker (null = niet ingelogd)
  loading: boolean       // Of er een auth request bezig is
  initialized: boolean   // Of de auth state al is geïnitialiseerd
}

// Reactive state object - wordt automatisch geüpdatet in alle components
const state = reactive<AuthState>({
  user: null,
  loading: false,
  initialized: false,
})

// Haalt huidige gebruiker op via sessie endpoint
async function fetchUser() {
  state.loading = true
  try {
    const res = await apiFetch('http://localhost:8080/api/auth/session')

    if (res.ok) {
      const data = await res.json()
      state.user = data  // Gebruiker is ingelogd
    } else {
      state.user = null  // Geen geldige sessie
    }
  } catch (e) {
    console.log(e)
    state.user = null
  } finally {
    state.loading = false
    state.initialized = true
  }
}

// Initialiseert auth state bij app start (wordt 1x aangeroepen)
async function initAuth() {
  if (state.initialized) return  // Voorkom dubbele init
  await fetchUser()
}

// Login functie - stuurt credentials naar backend
async function login(email: string, password: string) {
  state.loading = true
  try {
    const user = await loginRequest(email, password)
    state.user = user  // Sla ingelogde user op in state
  } finally {
    state.loading = false
    state.initialized = true
  }
}

// Logout functie - verwijdert sessie en herlaadt pagina
async function logout() {
  await logoutRequest()
  state.user = null
  location.reload()  // Refresh om alle state te resetten
}

// Exporteer store object voor gebruik in router guards
export const authStore = {
  state: readonly(state),  // Readonly voorkomt directe mutaties
  initAuth,
  login,
  logout,
}

// Composable hook voor gebruik in Vue components (bijv. AccountView)
// Retourneert reactive refs die automatisch updaten
export function useAuth() {
  const { user, loading, initialized } = toRefs(state)

  return {
    user,        // Ref naar ingelogde gebruiker
    loading,     // Ref naar loading state
    initialized, // Ref of auth is geïnitialiseerd
    login,       // Login functie
    logout,      // Logout functie
  }
}
