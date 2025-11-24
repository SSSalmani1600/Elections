<template>
  <div class="bg-[--color-background] min-h-screen text-white flex flex-col items-center py-20 px-4">
    <div class="text-center mb-12">
      <h1 class="text-4xl font-extrabold tracking-tight text-[--color-primary] mb-2">Mijn account</h1>
      <p class="text-gray-400">Bekijk of bewerk je persoonlijke gegevens</p>
    </div>

    <!-- Account info -->
    <div class="bg-[#111830] border border-[rgba(255,255,255,0.08)] rounded-2xl shadow-xl p-10 w-full max-w-2xl">
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

        <div>
          <p class="text-gray-400 text-sm uppercase tracking-wide">Admin</p>
          <p class="text-lg font-semibold">{{ user.is_admin ? 'Ja' : 'Nee' }}</p>
        </div>

        <div>
          <p class="text-gray-400 text-sm uppercase tracking-wide">Aangemaakt op</p>
          <p class="text-lg font-semibold">{{ user.created_at }}</p>
        </div>

        <button
          @click="editMode = true"
          class="px-7 py-3 rounded-xl font-semibold bg-gradient-to-r from-[#d82f4c] to-[#ef3054] text-white shadow-[0_2px_10px_rgba(239,48,84,0.15)] hover:shadow-[0_3px_15px_rgba(239,48,84,0.25)] hover:scale-[1.03] transition-all duration-200 ease-out border border-[rgba(255,255,255,0.08)] mt-10"
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
              class="w-full p-3 rounded-lg bg-[#0B132B] border border-gray-700 focus:outline-none focus:ring-2 focus:ring-[--color-primary]"
            />
          </div>

          <div>
            <label class="block text-gray-400 text-sm uppercase tracking-wide mb-1">Email</label>
            <input
              v-model="editUser.email"
              type="email"
              class="w-full p-3 rounded-lg bg-[#0B132B] border border-gray-700 focus:outline-none focus:ring-2 focus:ring-[--color-primary]"
            />
          </div>

          <div>
            <label class="block text-gray-400 text-sm uppercase tracking-wide mb-1">Nieuw wachtwoord</label>
            <input
              v-model="editUser.password"
              type="password"
              placeholder="Laat leeg om ongewijzigd te laten"
              class="w-full p-3 rounded-lg bg-[#0B132B] border border-gray-700 focus:outline-none focus:ring-2 focus:ring-[--color-primary]"
            />
          </div>

          <div class="mt-10 flex gap-4 justify-end">
            <button
              type="button"
              @click="cancelEdit"
              class="bg-gray-600 hover:bg-gray-700 px-6 py-3 rounded-xl transition-all duration-200 font-semibold"
            >
              Annuleren
            </button>
            <button
              type="submit"
              class="px-7 py-3 rounded-xl font-semibold bg-gradient-to-r from-[#d82f4c] to-[#ef3054] text-white shadow-[0_2px_10px_rgba(239,48,84,0.15)] hover:shadow-[0_3px_15px_rgba(239,48,84,0.25)] hover:scale-[1.03] transition-all duration-200 ease-out border border-[rgba(255,255,255,0.08)]"
            >
              Opslaan
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const editMode = ref(false)
const user = ref({
  username: 'nabil',
  email: 'nabil@gmail.com',
  is_admin: false,
  created_at: '2025-11-21 17:09:00',
})

const editUser = ref({ ...user.value, password: '' })

function saveChanges() {
  user.value = { ...editUser.value }
  editMode.value = false
  alert('✅ Gegevens succesvol opgeslagen!')
}

function cancelEdit() {
  editUser.value = { ...user.value, password: '' }
  editMode.value = false
}
</script>
