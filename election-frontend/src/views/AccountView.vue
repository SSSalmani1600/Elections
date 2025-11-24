<template>
  <div class="bg-[--color-background] min-h-screen text-white flex flex-col items-center py-20 px-4">
    <div class="text-center mb-12">
      <h1 class="text-4xl font-extrabold tracking-tight text-[--color-primary] mb-2">Mijn account</h1>
      <p class="text-gray-400">Bekijk of bewerk je persoonlijke gegevens</p>
    </div>

    <!-- Loading state -->
    <div v-if="loading && !user" class="bg-[#111830] border border-[rgba(255,255,255,0.08)] rounded-2xl shadow-xl p-10 w-full max-w-2xl text-center">
      <p class="text-gray-400">Gegevens laden...</p>
    </div>

    <!-- Account info -->
    <div v-else-if="user" class="bg-[#111830] border border-[rgba(255,255,255,0.08)] rounded-2xl shadow-xl p-10 w-full max-w-2xl">
      <div v-if="!editMode" class="space-y-4">
        <div>
          <p class="text-gray-400 text-sm uppercase tracking-wide">Gebruikersnaam</p>
          <p class="text-lg font-semibold">{{ user.username }}</p>
        </div>

        <div>
          <p class="text-gray-400 text-sm uppercase tracking-wide">Email</p>
          <a :href="`mailto:${user.email}`" class="text-lg font-semibold text-[--color-primary] hover:underline">
            {{ user.email }}
          </a>
        </div>

        <div v-if="user.isAdmin !== undefined">
          <p class="text-gray-400 text-sm uppercase tracking-wide">Admin</p>
          <p class="text-lg font-semibold">{{ user.isAdmin ? 'Ja' : 'Nee' }}</p>
        </div>

        <button
          @click="editMode = true"
          :disabled="loading"
          class="px-7 py-3 rounded-xl font-semibold bg-gradient-to-r from-[#d82f4c] to-[#ef3054] text-white shadow-[0_2px_10px_rgba(239,48,84,0.15)] hover:shadow-[0_3px_15px_rgba(239,48,84,0.25)] hover:scale-[1.03] transition-all duration-200 ease-out border border-[rgba(255,255,255,0.08)] mt-10 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Account bewerken
        </button>

      </div>

      <!-- Bewerken -->
      <div v-else>
        <form @submit.prevent="saveChanges" class="space-y-5">
          <div>
            <label class="block text-gray-400 text-sm uppercase tracking-wide mb-1">Gebruikersnaam</label>
            <input
              v-model="editUser.username"
              type="text"
              required
              class="w-full p-3 rounded-lg bg-[#0B132B] border border-gray-700 focus:outline-none focus:ring-2 focus:ring-[--color-primary] text-white"
            />
          </div>

          <div>
            <label class="block text-gray-400 text-sm uppercase tracking-wide mb-1">Email</label>
            <input
              v-model="editUser.email"
              type="email"
              required
              class="w-full p-3 rounded-lg bg-[#0B132B] border border-gray-700 focus:outline-none focus:ring-2 focus:ring-[--color-primary] text-white"
            />
          </div>

          <div>
            <label class="block text-gray-400 text-sm uppercase tracking-wide mb-1">Nieuw wachtwoord</label>
            <input
              v-model="editUser.password"
              type="password"
              placeholder="Laat leeg om ongewijzigd te laten"
              minlength="8"
              class="w-full p-3 rounded-lg bg-[#0B132B] border border-gray-700 focus:outline-none focus:ring-2 focus:ring-[--color-primary] text-white"
            />
            <p class="text-gray-500 text-xs mt-1">Minimaal 8 karakters</p>
          </div>

          <!-- Error/Success messages -->
          <div v-if="error" class="bg-red-900/30 border border-red-500 text-red-200 px-4 py-3 rounded-lg">
            {{ error }}
          </div>
          <div v-if="success" class="bg-green-900/30 border border-green-500 text-green-200 px-4 py-3 rounded-lg">
            {{ success }}
          </div>

          <div class="mt-10 flex gap-4 justify-end">
            <button
              type="button"
              @click="cancelEdit"
              :disabled="loading"
              class="bg-gray-600 hover:bg-gray-700 px-6 py-3 rounded-xl transition-all duration-200 font-semibold disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Annuleren
            </button>
            <button
              type="submit"
              :disabled="loading"
              class="px-7 py-3 rounded-xl font-semibold bg-gradient-to-r from-[#d82f4c] to-[#ef3054] text-white shadow-[0_2px_10px_rgba(239,48,84,0.15)] hover:shadow-[0_3px_15px_rgba(239,48,84,0.25)] hover:scale-[1.03] transition-all duration-200 ease-out border border-[rgba(255,255,255,0.08)] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ loading ? 'Opslaan...' : 'Opslaan' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCurrentUser, updateUser, type UpdateUserRequest } from '@/services/UserService'
import { useAuthStore } from '@/store/useAuthStore'
import { useRouter } from 'vue-router'
import type { User } from '@/types/api'

const router = useRouter()
const auth = useAuthStore()
const editMode = ref(false)
const loading = ref(false)
const error = ref('')
const success = ref('')

const user = ref<User | null>(null)
const editUser = ref({
  username: '',
  email: '',
  password: '',
})

// Haal gebruiker op bij mount
onMounted(async () => {
  const token = localStorage.getItem('JWT')

  // Check of gebruiker is ingelogd
  if (!token) {
    router.push('/inloggen')
    return
  }

  try {
    loading.value = true
    // Probeer eerst de huidige gebruiker op te halen via JWT token
    const userData = await getCurrentUser()
    user.value = userData
    editUser.value = {
      username: userData.username,
      email: userData.email,
      password: '',
    }
    // Update userId in localStorage voor backwards compatibility
    localStorage.setItem('userId', String(userData.id))
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Onbekende fout'
    console.error('Error fetching user:', err)

    // Check of het een 401 is (unauthorized/expired token)
    if (errorMsg.includes('401') || errorMsg.includes('Unauthorized') || errorMsg.includes('Invalid or expired')) {
      error.value = 'Je sessie is verlopen. Log opnieuw in om je account te bekijken.'
      // Geef gebruiker meer tijd om de error te lezen voordat redirect
      setTimeout(() => {
        auth.logout()
        router.push('/inloggen')
      }, 5000) // 5 seconden in plaats van 3
    } else if (errorMsg.includes('404') || errorMsg.includes('Not Found')) {
      error.value = 'Gebruiker niet gevonden. Mogelijk is je account verwijderd. Log opnieuw in.'
      setTimeout(() => {
        auth.logout()
        router.push('/inloggen')
      }, 5000)
    } else {
      error.value = `Fout: ${errorMsg}. Probeer de pagina te vernieuwen of log opnieuw in.`
      // Bij andere errors, niet direct uitloggen - misschien is het een tijdelijk probleem
      console.error('Non-auth error, not logging out automatically')
    }
  } finally {
    loading.value = false
  }
})

async function saveChanges() {
  if (!user.value) return

  error.value = ''
  success.value = ''
  loading.value = true

  try {
    const updates: UpdateUserRequest = {
      username: editUser.value.username,
      email: editUser.value.email,
    }

    // Alleen wachtwoord toevoegen als het is ingevuld
    if (editUser.value.password && editUser.value.password.length > 0) {
      if (editUser.value.password.length < 8) {
        error.value = 'Wachtwoord moet minimaal 8 karakters zijn'
        loading.value = false
        return
      }
      updates.password = editUser.value.password
    }

    const updatedUser = await updateUser(user.value.id, updates)
    user.value = updatedUser

    // Update auth store als username is gewijzigd
    if (updatedUser.username !== auth.user) {
      auth.login(updatedUser.username, localStorage.getItem('JWT') || '')
    }

    success.value = '✅ Gegevens succesvol opgeslagen!'
    editMode.value = false
    editUser.value.password = '' // Reset wachtwoord veld
  } catch (err: unknown) {
    const errorMessage = err instanceof Error ? err.message : 'Kon gegevens niet opslaan. Probeer het opnieuw.'
    error.value = errorMessage
    console.error(err)
  } finally {
    loading.value = false
  }
}

function cancelEdit() {
  if (user.value) {
    editUser.value = {
      username: user.value.username,
      email: user.value.email,
      password: '',
    }
  }
  editMode.value = false
  error.value = ''
  success.value = ''
}
</script>
