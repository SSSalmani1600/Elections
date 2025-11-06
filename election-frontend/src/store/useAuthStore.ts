import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<string | null>(localStorage.getItem('username'))
  const token = ref<string | null>(localStorage.getItem('JWT'))

  const isLoggedIn = computed(() => !!token.value)

  function login(username: string, jwt: string) {
    user.value = username
    token.value = jwt
    localStorage.setItem('JWT', jwt)
    localStorage.setItem('username', username)
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('JWT')
    localStorage.removeItem('username')
  }

  return { user, token, isLoggedIn, login, logout }
})
