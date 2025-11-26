<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/services/AuthService'
import type { LoginResponse, RegisterResponse } from '@/types/api'
import { useAuthStore } from '@/store/useAuthStore'

const router = useRouter()
const auth = useAuthStore()

const displayName = ref('')
const email = ref('')
const password = ref('')
const agree = ref(false)
const error = ref('')
const success = ref('')
const loading = ref(false)

async function onSubmit() {
  error.value = ''
  success.value = ''
  loading.value = true
  try {
    if (!agree.value) {
      throw new Error('Je moet akkoord gaan met de voorwaarden.')
    }

    const res: RegisterResponse = await register(email.value, password.value, displayName.value)
    const data: LoginResponse = await login(res.email, res.password)

    // ✅ Sla alle benodigde data op in localStorage
    localStorage.setItem("JWT", data.token)
    localStorage.setItem("userId", String(data.id))
    localStorage.setItem("username", data.displayName)

    // ✅ log direct in via de store
    auth.login(res.username, data.token)
    
    // ✅ Sla userId op voor account pagina
    if (data.id) {
      localStorage.setItem('userId', String(data.id))
    }

    success.value = `Account aangemaakt en automatisch ingelogd als ${res.username}!`
    await router.replace('/') // geen timeout
  } catch (err) {
    console.error(err)
    error.value = 'Registratie mislukt. Controleer je gegevens.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-[#1C2541] flex flex-col items-center justify-center px-4">
    <div
      class="bg-[#0B132B] p-8 rounded-2xl shadow-lg w-full max-w-md flex flex-col gap-6 text-white"
    >
      <h1 class="text-3xl font-bold text-center">Registreren</h1>

      <form @submit.prevent="onSubmit" class="flex flex-col gap-5">
        <!-- Display Name -->
        <div class="flex flex-col gap-2">
          <label for="displayName" class="text-sm font-medium text-gray-300"
          >Naam/gebruikersnaam</label
          >
          <input
            v-model="displayName"
            type="text"
            id="displayName"
            required
            class="bg-[#0c0f2a] border border-[#30335a] rounded-lg p-3 text-white focus:outline-none focus:ring-2 focus:ring-[#EF3054]"
            placeholder="Naam/gebruikersnaam"
          />
        </div>

        <!-- Email -->
        <div class="flex flex-col gap-2">
          <label for="email" class="text-sm font-medium text-gray-300">E-mail</label>
          <input
            v-model="email"
            type="email"
            id="email"
            required
            class="bg-[#0c0f2a] border border-[#30335a] rounded-lg p-3 text-white focus:outline-none focus:ring-2 focus:ring-[#EF3054]"
            placeholder="E-mail"
          />
        </div>

        <!-- Password -->
        <div class="flex flex-col gap-2">
          <label for="password" class="text-sm font-medium text-gray-300">Wachtwoord</label>
          <input
            v-model="password"
            type="password"
            id="password"
            required
            minlength="8"
            class="bg-[#0c0f2a] border border-[#30335a] rounded-lg p-3 text-white focus:outline-none focus:ring-2 focus:ring-[#EF3054]"
            placeholder="Wachtwoord"
          />
        </div>

        <!-- Checkbox -->
        <div class="flex items-center gap-2 text-sm">
          <input id="agree" type="checkbox" v-model="agree" class="w-4 h-4 accent-[#EF3054]" />
          <label for="agree">
            Ik ga akkoord met de
            <span class="text-[#EF3054] cursor-pointer">voorwaarden</span>
            en
            <span class="text-[#EF3054] cursor-pointer">privacyverklaring</span>.
          </label>
        </div>

        <!-- Submit Button -->
        <button
          type="submit"
          :disabled="loading"
          class="bg-[#EF3054] hover:bg-[#D9294B] text-white py-3 rounded-lg font-semibold shadow-md hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-[#EF3054]/40 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ loading ? 'Bezig...' : 'Registreer' }}
        </button>
      </form>

      <!-- Meldingen -->
      <p v-if="success" class="text-green-400 text-center font-medium">{{ success }}</p>
      <p v-if="error" class="text-[#EF3054] text-center font-medium mt-2">
        {{ error }}
      </p>

      <p class="text-sm text-gray-300 text-center">
        Heb je al een account?
        <router-link to="/login" class="text-[#EF3054] hover:underline">Inloggen</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
input::placeholder {
  color: #777;
}
</style>

