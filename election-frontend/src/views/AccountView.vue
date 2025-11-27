<template>
  <div class="min-h-screen bg-[--color-background] text-white">
    <div class="max-w-6xl mx-auto py-16 px-4">
      <!-- Header -->
      <div class="text-center mb-10">
        <div
          class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gradient-to-br from-[#ef3054] to-[#d82f4c] mb-6 shadow-lg shadow-[#ef3054]/20"
        >
          <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
            />
          </svg>
        </div>
        <h1 class="text-4xl font-bold tracking-tight mb-2">Mijn Account</h1>
        <p class="text-gray-400 text-lg">Beheer je gegevens en bekijk je activiteit</p>
      </div>

      <!-- Loading state -->
      <div v-if="loading && !user" class="flex justify-center">
        <div class="bg-[#111830]/80 border border-white/10 rounded-2xl p-10 text-center">
          <div
            class="animate-spin w-12 h-12 border-4 border-[#ef3054] border-t-transparent rounded-full mx-auto mb-4"
          ></div>
          <p class="text-gray-400">Gegevens laden...</p>
        </div>
      </div>

      <!-- Error state -->
      <div v-else-if="!user && error" class="flex justify-center">
        <div class="bg-red-900/20 border border-red-500/30 rounded-2xl p-8 text-center max-w-xl">
          <svg
            class="w-16 h-16 text-red-400 mx-auto mb-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
            />
          </svg>
          <p class="text-red-200 text-lg">{{ error }}</p>
        </div>
      </div>

      <!-- Main Content: Two Column Layout -->
      <div v-else-if="user" class="grid lg:grid-cols-2 gap-8 items-start">
        <!-- Left Column: Account Info (Sticky) -->
        <div
          class="bg-[#111830]/80 border border-white/10 rounded-2xl shadow-xl overflow-hidden lg:sticky lg:top-8"
        >
          <!-- View Mode -->
          <div v-if="!editMode">
            <!-- User header -->
            <div
              class="bg-gradient-to-r from-[#ef3054]/20 to-[#d82f4c]/10 p-6 border-b border-white/5"
            >
              <div class="flex items-center gap-4">
                <div
                  class="w-14 h-14 rounded-full bg-gradient-to-br from-[#ef3054] to-[#d82f4c] flex items-center justify-center text-xl font-bold shadow-lg"
                >
                  {{ user.username.charAt(0).toUpperCase() }}
                </div>
                <div>
                  <h2 class="text-xl font-bold">{{ user.username }}</h2>
                  <p class="text-gray-400 text-sm">{{ user.email }}</p>
                </div>
              </div>
            </div>

            <!-- Account details -->
            <div class="p-6 space-y-4">
              <div
                class="flex items-center justify-between p-3 rounded-xl bg-white/5 border border-white/5"
              >
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-lg bg-[#ef3054]/20 flex items-center justify-center">
                    <svg
                      class="w-4 h-4 text-[#ef3054]"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                      />
                    </svg>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500 uppercase tracking-wider">Gebruikersnaam</p>
                    <p class="font-medium text-sm">{{ user.username }}</p>
                  </div>
                </div>
              </div>

              <div
                class="flex items-center justify-between p-3 rounded-xl bg-white/5 border border-white/5"
              >
                <div class="flex items-center gap-3">
                  <div class="w-9 h-9 rounded-lg bg-blue-500/20 flex items-center justify-center">
                    <svg
                      class="w-4 h-4 text-blue-400"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                      />
                    </svg>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500 uppercase tracking-wider">E-mailadres</p>
                    <p class="font-medium text-sm">{{ user.email }}</p>
                  </div>
                </div>
              </div>

              <div
                class="flex items-center justify-between p-3 rounded-xl bg-white/5 border border-white/5"
              >
                <div class="flex items-center gap-3">
                  <div
                    class="w-9 h-9 rounded-lg bg-emerald-500/20 flex items-center justify-center"
                  >
                    <svg
                      class="w-4 h-4 text-emerald-400"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"
                      />
                    </svg>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500 uppercase tracking-wider">Account type</p>
                    <p class="font-medium text-sm">
                      {{ user.isAdmin ? 'Beheerder' : 'Gebruiker' }}
                    </p>
                  </div>
                </div>
                <span
                  v-if="user.isAdmin"
                  class="px-2 py-1 text-xs font-medium bg-emerald-500/20 text-emerald-400 rounded-full border border-emerald-500/30"
                >
                  Admin
                </span>
              </div>

              <button
                @click="startEdit"
                class="w-full mt-4 px-5 py-3 rounded-xl cursor-pointer font-semibold bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white shadow-lg shadow-[#ef3054]/20 hover:shadow-xl hover:shadow-[#ef3054]/30 hover:scale-[1.02] transition-all duration-300 flex items-center justify-center gap-2 text-sm"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                  />
                </svg>
                Account bewerken
              </button>
            </div>
          </div>

          <!-- Edit Mode -->
          <div v-else class="p-6">
            <div class="flex items-center gap-3 mb-6">
              <button
                @click="cancelEdit"
                class="p-2 rounded-lg hover:bg-white/10 transition-colors"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M10 19l-7-7m0 0l7-7m-7 7h18"
                  />
                </svg>
              </button>
              <div>
                <h2 class="text-xl font-bold">Gegevens bewerken</h2>
                <p class="text-gray-400 text-xs">Bevestig met je huidige wachtwoord</p>
              </div>
            </div>

            <form @submit.prevent="saveChanges" class="space-y-4">
              <!-- Current password -->
              <div class="p-4 rounded-xl bg-amber-500/10 border border-amber-500/30">
                <div class="flex items-center gap-2 mb-3">
                  <svg
                    class="w-5 h-5 text-amber-400"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                    />
                  </svg>
                  <span class="font-medium text-amber-200 text-sm">Huidig wachtwoord</span>
                </div>
                <input
                  v-model="currentPassword"
                  type="password"
                  required
                  placeholder="Je huidige wachtwoord"
                  class="w-full p-3 rounded-lg bg-[#0B132B] border border-amber-500/30 focus:border-amber-400 focus:outline-none text-white placeholder-gray-500 text-sm"
                />
              </div>

              <div>
                <label class="text-xs font-medium text-gray-400 mb-1 block">Gebruikersnaam</label>
                <input
                  v-model="editUser.username"
                  type="text"
                  required
                  class="w-full p-3 rounded-lg bg-[#0B132B] border border-white/10 focus:border-[#ef3054] focus:outline-none text-white text-sm"
                />
              </div>

              <div>
                <label class="text-xs font-medium text-gray-400 mb-1 block">E-mailadres</label>
                <input
                  v-model="editUser.email"
                  type="email"
                  required
                  class="w-full p-3 rounded-lg bg-[#0B132B] border border-white/10 focus:border-[#ef3054] focus:outline-none text-white text-sm"
                />
              </div>

              <div>
                <label class="text-xs font-medium text-gray-400 mb-1 block"
                  >Nieuw wachtwoord <span class="text-gray-600">(optioneel)</span></label
                >
                <input
                  v-model="editUser.newPassword"
                  type="password"
                  placeholder="Minimaal 8 karakters"
                  minlength="8"
                  class="w-full p-3 rounded-lg bg-[#0B132B] border border-white/10 focus:border-[#ef3054] focus:outline-none text-white placeholder-gray-500 text-sm"
                />
              </div>

              <div
                v-if="error"
                class="p-3 rounded-lg bg-red-500/10 border border-red-500/30 text-red-200 text-sm"
              >
                {{ error }}
              </div>
              <div
                v-if="success"
                class="p-3 rounded-lg bg-emerald-500/10 border border-emerald-500/30 text-emerald-200 text-sm"
              >
                {{ success }}
              </div>

              <div class="flex gap-3 pt-2">
                <button
                  type="button"
                  @click="cancelEdit"
                  :disabled="loading"
                  class="flex-1 px-4 py-3 rounded-lg cursor-pointer font-medium bg-white/5 border border-white/10 hover:bg-white/10 transition-all text-sm"
                >
                  Annuleren
                </button>
                <button
                  type="submit"
                  :disabled="loading || !currentPassword"
                  class="flex-1 px-4 py-3 rounded-lg font-medium bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white transition-all cursor-pointer disabled:cursor-not-allowed disabled:opacity-50 text-sm"
                >
                  {{ loading ? 'Opslaan...' : 'Opslaan' }}
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- Right Column: Activity -->
        <div class="space-y-6">
          <!-- Topics -->
          <div class="bg-[#111830]/80 border border-white/10 rounded-2xl shadow-xl overflow-hidden">
            <div class="p-5 border-b border-white/5 flex items-center gap-3">
              <div class="w-9 h-9 rounded-lg bg-purple-500/20 flex items-center justify-center">
                <svg
                  class="w-5 h-5 text-purple-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"
                  />
                </svg>
              </div>
              <div>
                <h3 class="font-semibold">Mijn Topics</h3>
                <p class="text-gray-500 text-xs">{{ activity.topics.length }} geplaatst</p>
              </div>
            </div>
            <div class="p-4 max-h-[280px] overflow-y-auto">
              <div v-if="activityLoading" class="text-center py-6 text-gray-500 text-sm">
                Laden...
              </div>
              <div
                v-else-if="activity.topics.length === 0"
                class="text-center py-6 text-gray-500 text-sm italic"
              >
                Nog geen topics geplaatst
              </div>
              <div v-else class="space-y-2">
                <router-link
                  v-for="topic in activity.topics"
                  :key="topic.id"
                  :to="`/discussions/${topic.id}`"
                  class="block p-3 rounded-xl bg-white/5 border border-white/5 hover:border-purple-500/30 hover:bg-purple-500/5 transition-all"
                >
                  <p class="font-medium text-sm truncate">{{ topic.title }}</p>
                  <div class="flex items-center gap-3 mt-1 text-xs text-gray-500">
                    <span>{{ formatDate(topic.createdAt) }}</span>
                    <span>•</span>
                    <span>{{ topic.reactionsCount }} reacties</span>
                  </div>
                </router-link>
              </div>
            </div>
          </div>

          <!-- Reactions -->
          <div class="bg-[#111830]/80 border border-white/10 rounded-2xl shadow-xl overflow-hidden">
            <div class="p-5 border-b border-white/5 flex items-center gap-3">
              <div class="w-9 h-9 rounded-lg bg-cyan-500/20 flex items-center justify-center">
                <svg
                  class="w-5 h-5 text-cyan-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
                  />
                </svg>
              </div>
              <div>
                <h3 class="font-semibold">Mijn Reacties</h3>
                <p class="text-gray-500 text-xs">{{ activity.reactions.length }} geplaatst</p>
              </div>
            </div>
            <div class="p-4 max-h-[280px] overflow-y-auto">
              <div v-if="activityLoading" class="text-center py-6 text-gray-500 text-sm">
                Laden...
              </div>
              <div
                v-else-if="activity.reactions.length === 0"
                class="text-center py-6 text-gray-500 text-sm italic"
              >
                Nog geen reacties geplaatst
              </div>
              <div v-else class="space-y-2">
                <router-link
                  v-for="reaction in activity.reactions"
                  :key="reaction.id"
                  :to="`/discussions/${reaction.discussionId}`"
                  class="block p-3 rounded-xl bg-white/5 border border-white/5 hover:border-cyan-500/30 hover:bg-cyan-500/5 transition-all"
                >
                  <p class="text-sm text-gray-300 line-clamp-2">{{ reaction.message }}</p>
                  <div class="flex items-center gap-2 mt-2 text-xs text-gray-500">
                    <span class="text-cyan-400/70">↳</span>
                    <span class="truncate">{{ reaction.discussionTitle }}</span>
                  </div>
                </router-link>
              </div>
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
import { useRouter } from 'vue-router'
import { useAuth } from '@/store/authStore'

const router = useRouter()
const editMode = ref(false)
const loading = ref(false)
const activityLoading = ref(false)
const error = ref('')
const success = ref('')

const { user, logout, initialized } = useAuth()

const currentPassword = ref('')
const editUser = ref({
  username: '',
  email: '',
  newPassword: '',
})

interface Topic {
  id: number
  title: string
  createdAt: string
  reactionsCount: number
}

interface Reaction {
  id: number
  message: string
  createdAt: string
  discussionId: number
  discussionTitle: string
}

const activity = ref<{ topics: Topic[]; reactions: Reaction[] }>({
  topics: [],
  reactions: [],
})

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('nl-NL', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

async function fetchActivity(userId: number) {
  activityLoading.value = true
  try {
    const res = await fetch(`http://localhost:8080/api/users/${userId}/activity`)
    if (res.ok) {
      activity.value = await res.json()
    }
  } catch (err) {
    console.error('Error fetching activity:', err)
  } finally {
    activityLoading.value = false
  }
}

onMounted(async () => {
  if (initialized.value && !user.value) {
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

    // Fetch activity
    await fetchActivity(userData.id)
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Onbekende fout'
    console.error('Error fetching user:', err)

    if (
      errorMsg.includes('401') ||
      errorMsg.includes('Unauthorized') ||
      errorMsg.includes('Invalid or expired')
    ) {
      error.value = 'Je sessie is verlopen. Log opnieuw in.'
      setTimeout(async () => {}, 3000)
    } else if (errorMsg.includes('404') || errorMsg.includes('Not Found')) {
      error.value = 'Gebruiker niet gevonden.'
      setTimeout(async () => {}, 3000)
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
    error.value = 'Voer je huidige wachtwoord in.'
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

    // if (updatedUser.username !== user.username) {
    //   login(updatedUser.username, localStorage.getItem('JWT') || '')
    //   localStorage.setItem('username', updatedUser.username)
    // }

    success.value = 'Gegevens opgeslagen!'
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

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
