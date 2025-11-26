import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<string | null>(localStorage.getItem('username'))
  const token = ref<string | null>(localStorage.getItem('JWT'))
  const isAdmin = ref<boolean>(localStorage.getItem("isAdmin") === "true")

  const isLoggedIn = computed(() => !!token.value)

  function login(username: string, jwt: string, admin: boolean) {
    user.value = username
    token.value = jwt
    isAdmin.value = admin

    localStorage.setItem("JWT", jwt)
    localStorage.setItem("username", username)
    localStorage.setItem("isAdmin", String(admin))
  }


  function logout() {
    user.value = null
    token.value = null
    isAdmin.value = false

    localStorage.removeItem('JWT')
    localStorage.removeItem('username')
    localStorage.removeItem('isAdmin')
    window.location.href = "/";
  }

  return { user, token, isAdmin, isLoggedIn, login, logout }
})
