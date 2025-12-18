<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { type AdminPoll, createPoll, getAllAdminPolls } from '@/services/PollService'

const question = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  success.value = ''

  if (!question.value.trim()) {
    error.value = 'De vraag mag niet leeg zijn.'
    return
  }

  loading.value = true
  try {
    await createPoll(question.value)
    success.value = 'Nieuwe stelling succesvol geplaatst.'
    question.value = ''
  } catch (e: any) {
    error.value = e.message || 'Opslaan mislukt'
  } finally {
    loading.value = false
  }
}

const polls = ref<AdminPoll[]>([])

onMounted(async () => {
  polls.value = await getAllAdminPolls()
  loading.value = false
})
</script>

<template>
  <AdminLayout>
    <h1 class="text-3xl font-bold mb-6 text-white">Stelling toevoegen</h1>

    <p v-if="error" class="text-red-400 mb-3">{{ error }}</p>
    <p v-if="success" class="text-green-400 mb-3">{{ success }}</p>

    <textarea
      v-model="question"
      rows="3"
      class="w-full p-3 rounded-lg bg-white/10 text-white"
      placeholder="Voer hier de stelling in..."
    />

    <button class="btn btn-primary mt-4" :disabled="loading" @click="submit">
      {{ loading ? 'Opslaan...' : 'Plaatsen' }}
    </button>
    <h2 class="text-2xl font-bold mt-12 mb-4">Bestaande stellingen</h2>

    <div v-if="loading" class="text-gray-400">Laden...</div>

    <div v-else class="space-y-4">
      <div v-for="poll in polls" :key="poll.id" class="bg-white/10 p-5 rounded-xl">
        <p class="font-semibold text-lg mb-1">{{ poll.question }}</p>
        <p class="text-sm text-gray-400 mb-2">
          Aangemaakt op: {{ new Date(poll.createdAt).toLocaleDateString() }}
        </p>

        <div class="flex gap-4 text-sm">
          <span class="text-green-400"> Eens: {{ poll.eensPercentage }}% </span>
          <span class="text-red-400"> Oneens: {{ poll.oneensPercentage }}% </span>
          <span class="text-gray-300"> ({{ poll.total }} stemmen) </span>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>
