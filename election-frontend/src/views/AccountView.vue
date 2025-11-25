<template>
  <div class="min-h-screen bg-gradient-to-br from-[#0B132B] via-[#1C2541] to-[#0B132B] text-white">
    <!-- Background pattern -->
    <div class="absolute inset-0 opacity-5">
      <div class="absolute inset-0" style="background-image: url('data:image/svg+xml,%3Csvg width=\'60\' height=\'60\' viewBox=\'0 0 60 60\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cg fill=\'none\' fill-rule=\'evenodd\'%3E%3Cg fill=\'%23ffffff\' fill-opacity=\'0.4\'%3E%3Cpath d=\'M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z\'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E');"></div>
    </div>

    <div class="relative z-10 flex flex-col items-center py-16 px-4">
      <!-- Header -->
      <div class="text-center mb-10">
        <div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gradient-to-br from-[#ef3054] to-[#d82f4c] mb-6 shadow-lg shadow-[#ef3054]/20">
          <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
        </div>
        <h1 class="text-4xl font-bold tracking-tight mb-2">Mijn Account</h1>
        <p class="text-gray-400 text-lg">Beheer je persoonlijke gegevens</p>
      </div>

      <!-- Loading state -->
      <div v-if="loading && !user" class="w-full max-w-xl">
        <div class="bg-[#111830]/80 backdrop-blur-xl border border-white/10 rounded-2xl p-10 text-center shadow-2xl">
          <div class="animate-spin w-12 h-12 border-4 border-[#ef3054] border-t-transparent rounded-full mx-auto mb-4"></div>
          <p class="text-gray-400">Gegevens laden...</p>
        </div>
      </div>

      <!-- Error state (not logged in) -->
      <div v-else-if="!user && error" class="w-full max-w-xl">
        <div class="bg-red-900/20 backdrop-blur-xl border border-red-500/30 rounded-2xl p-8 text-center shadow-2xl">
          <svg class="w-16 h-16 text-red-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <p class="text-red-200 text-lg">{{ error }}</p>
        </div>
      </div>

      <!-- Account Card -->
      <div v-else-if="user" class="w-full max-w-xl">
        <div class="bg-[#111830]/80 backdrop-blur-xl border border-white/10 rounded-2xl shadow-2xl overflow-hidden">
          
          <!-- View Mode -->
          <div v-if="!editMode">
            <!-- User header -->
            <div class="bg-gradient-to-r from-[#ef3054]/20 to-[#d82f4c]/10 p-8 border-b border-white/5">
              <div class="flex items-center gap-5">
                <div class="w-16 h-16 rounded-full bg-gradient-to-br from-[#ef3054] to-[#d82f4c] flex items-center justify-center text-2xl font-bold shadow-lg">
                  {{ user.username.charAt(0).toUpperCase() }}
                </div>
                <div>
                  <h2 class="text-2xl font-bold">{{ user.username }}</h2>
                  <p class="text-gray-400">{{ user.email }}</p>
                </div>
              </div>
            </div>

            <!-- Account details -->
            <div class="p-8 space-y-6">
              <div class="grid gap-6">
                <div class="flex items-center justify-between p-4 rounded-xl bg-white/5 border border-white/5">
                  <div class="flex items-center gap-4">
                    <div class="w-10 h-10 rounded-lg bg-[#ef3054]/20 flex items-center justify-center">
                      <svg class="w-5 h-5 text-[#ef3054]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                    </div>
                    <div>
                      <p class="text-xs text-gray-500 uppercase tracking-wider">Gebruikersnaam</p>
                      <p class="font-semibold">{{ user.username }}</p>
                    </div>
                  </div>
                </div>

                <div class="flex items-center justify-between p-4 rounded-xl bg-white/5 border border-white/5">
                  <div class="flex items-center gap-4">
                    <div class="w-10 h-10 rounded-lg bg-blue-500/20 flex items-center justify-center">
                      <svg class="w-5 h-5 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                      </svg>
                    </div>
                    <div>
                      <p class="text-xs text-gray-500 uppercase tracking-wider">E-mailadres</p>
                      <p class="font-semibold">{{ user.email }}</p>
                    </div>
                  </div>
                </div>

                <div class="flex items-center justify-between p-4 rounded-xl bg-white/5 border border-white/5">
                  <div class="flex items-center gap-4">
                    <div class="w-10 h-10 rounded-lg bg-emerald-500/20 flex items-center justify-center">
                      <svg class="w-5 h-5 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                      </svg>
                    </div>
                    <div>
                      <p class="text-xs text-gray-500 uppercase tracking-wider">Account type</p>
                      <p class="font-semibold">{{ user.isAdmin ? 'Beheerder' : 'Gebruiker' }}</p>
                    </div>
                  </div>
                  <span v-if="user.isAdmin" class="px-3 py-1 text-xs font-medium bg-emerald-500/20 text-emerald-400 rounded-full border border-emerald-500/30">
                    Admin
                  </span>
                </div>
              </div>

              <!-- Edit button -->
              <button
                @click="startEdit"
                class="w-full mt-6 px-6 py-4 rounded-xl font-semibold bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white 
                       shadow-lg shadow-[#ef3054]/20 hover:shadow-xl hover:shadow-[#ef3054]/30 
                       hover:scale-[1.02] transition-all duration-300 
                       flex items-center justify-center gap-3"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
                Account bewerken
              </button>
            </div>
          </div>

          <!-- Edit Mode -->
          <div v-else>
            <div class="p-8">
              <div class="flex items-center gap-4 mb-8">
                <button @click="cancelEdit" class="p-2 rounded-lg hover:bg-white/10 transition-colors">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                  </svg>
                </button>
                <div>
                  <h2 class="text-2xl font-bold">Gegevens bewerken</h2>
                  <p class="text-gray-400 text-sm">Voer je huidige wachtwoord in om wijzigingen te bevestigen</p>
                </div>
              </div>

              <form @submit.prevent="saveChanges" class="space-y-6">
                <!-- Current password (required for security) -->
                <div class="p-5 rounded-xl bg-amber-500/10 border border-amber-500/30 mb-6">
                  <div class="flex items-start gap-3 mb-4">
                    <svg class="w-6 h-6 text-amber-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                    </svg>
                    <div>
                      <p class="font-semibold text-amber-200">Bevestig je identiteit</p>
                      <p class="text-amber-200/70 text-sm">Voor je veiligheid moet je je huidige wachtwoord invoeren</p>
                    </div>
                  </div>
                  <input
                    v-model="currentPassword"
                    type="password"
                    required
                    placeholder="Je huidige wachtwoord"
                    class="w-full p-4 rounded-xl bg-[#0B132B] border border-amber-500/30 focus:border-amber-400 focus:ring-2 focus:ring-amber-400/20 focus:outline-none text-white placeholder-gray-500 transition-all"
                  />
                </div>

                <!-- Username -->
                <div>
                  <label class="flex items-center gap-2 text-sm font-medium text-gray-300 mb-2">
                    <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                    Gebruikersnaam
                  </label>
                  <input
                    v-model="editUser.username"
                    type="text"
                    required
                    class="w-full p-4 rounded-xl bg-[#0B132B] border border-white/10 focus:border-[#ef3054] focus:ring-2 focus:ring-[#ef3054]/20 focus:outline-none text-white transition-all"
                  />
                </div>

                <!-- Email -->
                <div>
                  <label class="flex items-center gap-2 text-sm font-medium text-gray-300 mb-2">
                    <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                    E-mailadres
                  </label>
                  <input
                    v-model="editUser.email"
                    type="email"
                    required
                    class="w-full p-4 rounded-xl bg-[#0B132B] border border-white/10 focus:border-[#ef3054] focus:ring-2 focus:ring-[#ef3054]/20 focus:outline-none text-white transition-all"
                  />
                </div>

                <!-- New password -->
                <div>
                  <label class="flex items-center gap-2 text-sm font-medium text-gray-300 mb-2">
                    <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                    </svg>
                    Nieuw wachtwoord
                    <span class="text-gray-500 font-normal">(optioneel)</span>
                  </label>
                  <input
                    v-model="editUser.newPassword"
                    type="password"
                    placeholder="Laat leeg om ongewijzigd te laten"
                    minlength="8"
                    class="w-full p-4 rounded-xl bg-[#0B132B] border border-white/10 focus:border-[#ef3054] focus:ring-2 focus:ring-[#ef3054]/20 focus:outline-none text-white placeholder-gray-500 transition-all"
                  />
                  <p class="text-gray-500 text-xs mt-2 flex items-center gap-1">
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    Minimaal 8 karakters
                  </p>
                </div>

                <!-- Messages -->
                <div v-if="error" class="flex items-center gap-3 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-200">
                  <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  {{ error }}
                </div>
                
                <div v-if="success" class="flex items-center gap-3 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-200">
                  <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  {{ success }}
                </div>

                <!-- Buttons -->
                <div class="flex gap-4 pt-4">
                  <button
                    type="button"
                    @click="cancelEdit"
                    :disabled="loading"
                    class="flex-1 px-6 py-4 rounded-xl font-semibold bg-white/5 border border-white/10 
                           hover:bg-white/10 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Annuleren
                  </button>
                  <button
                    type="submit"
                    :disabled="loading || !currentPassword"
                    class="flex-1 px-6 py-4 rounded-xl font-semibold bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white 
                           shadow-lg shadow-[#ef3054]/20 hover:shadow-xl hover:shadow-[#ef3054]/30 
                           transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed
                           flex items-center justify-center gap-2"
                  >
                    <span v-if="loading" class="animate-spin w-5 h-5 border-2 border-white border-t-transparent rounded-full"></span>
                    <span v-else>
                      <svg class="w-5 h-5 inline mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                      </svg>
                      Wijzigingen opslaan
                    </span>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
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
const currentPassword = ref('')
const editUser = ref({
  username: '',
  email: '',
  newPassword: '',
})

onMounted(async () => {
  const token = localStorage.getItem('JWT')
  if (!token) {
    router.push('/inloggen')
    return
  }

  try {
    loading.value = true
    const userData = await getCurrentUser()
    user.value = userData
    editUser.value = {
      username: userData.username,
      email: userData.email,
      newPassword: '',
    }
    localStorage.setItem('userId', String(userData.id))
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Onbekende fout'
    console.error('Error fetching user:', err)

    if (errorMsg.includes('401') || errorMsg.includes('Unauthorized') || errorMsg.includes('Invalid or expired')) {
      error.value = 'Je sessie is verlopen. Log opnieuw in.'
      setTimeout(() => {
        auth.logout()
        router.push('/inloggen')
      }, 3000)
    } else if (errorMsg.includes('404') || errorMsg.includes('Not Found')) {
      error.value = 'Gebruiker niet gevonden.'
      setTimeout(() => {
        auth.logout()
        router.push('/inloggen')
      }, 3000)
    } else {
      error.value = `Fout: ${errorMsg}`
    }
  } finally {
    loading.value = false
  }
})

function startEdit() {
  editMode.value = true
  currentPassword.value = ''
  error.value = ''
  success.value = ''
}

async function saveChanges() {
  if (!user.value) return
  if (!currentPassword.value) {
    error.value = 'Voer je huidige wachtwoord in om wijzigingen te bevestigen.'
    return
  }

  error.value = ''
  success.value = ''
  loading.value = true

  try {
    const updates: UpdateUserRequest = {
      username: editUser.value.username,
      email: editUser.value.email,
      currentPassword: currentPassword.value,
    }

    if (editUser.value.newPassword && editUser.value.newPassword.length > 0) {
      if (editUser.value.newPassword.length < 8) {
        error.value = 'Nieuw wachtwoord moet minimaal 8 karakters zijn'
        loading.value = false
        return
      }
      updates.password = editUser.value.newPassword
    }

    const updatedUser = await updateUser(user.value.id, updates)
    user.value = updatedUser

    if (updatedUser.username !== auth.user) {
      auth.login(updatedUser.username, localStorage.getItem('JWT') || '')
      localStorage.setItem('username', updatedUser.username)
    }

    success.value = 'Gegevens succesvol opgeslagen!'
    editMode.value = false
    currentPassword.value = ''
    editUser.value.newPassword = ''
  } catch (err: unknown) {
    const errorMessage = err instanceof Error ? err.message : 'Kon gegevens niet opslaan.'
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
      newPassword: '',
    }
  }
  currentPassword.value = ''
  editMode.value = false
  error.value = ''
  success.value = ''
}
</script>
