// src/stores/useAuth.ts
import { ref } from "vue"

// ✅ Reactieve state
const isLoggedIn = ref(!!localStorage.getItem("token"))
const displayName = ref(localStorage.getItem("displayName") || "")

// ✅ Functies om loginstatus te beheren
export function useAuth() {
    function login(token: string, name: string) {
        localStorage.setItem("token", token)
        localStorage.setItem("displayName", name)
        isLoggedIn.value = true
        displayName.value = name
    }

    function logout() {
        localStorage.removeItem("token")
        localStorage.removeItem("displayName")
        isLoggedIn.value = false
        displayName.value = ""
    }

    return { isLoggedIn, displayName, login, logout }
}
