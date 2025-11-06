<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

type DiscussionListItem = {
  id: string
  title: string
  author: string
  lastActivityAt: string
  reactionsCount: number
}

const router = useRouter()

const discussions = ref<DiscussionListItem[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// Modal state
const showModal = ref(false)
const submitting = ref(false)
const formTitle = ref('')
const formBody = ref('')
const formError = ref<string | null>(null)

onMounted(async () => {
  try {
    const res = await fetch('http://localhost:8080/api/discussions')
    if (!res.ok) {
      throw new Error('Kon discussies niet laden')
    }
    discussions.value = await res.json()
  } catch (e: any) {
    error.value = e.message ?? 'Er ging iets mis bij het ophalen'
  } finally {
    loading.value = false
  }
})

function openModal() {
  formTitle.value = ''
  formBody.value = ''
  formError.value = null
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

function goToDetail(id: string) {
  router.push(`/discussions/${id}`)
}

async function createDiscussion() {
  if (!formTitle.value.trim() || !formBody.value.trim()) {
    formError.value = 'Titel en bericht mogen niet leeg zijn.'
    return
  }

  submitting.value = true
  formError.value = null

  try {
    const res = await fetch('http://localhost:8080/api/discussions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        title: formTitle.value.trim(),
        body: formBody.value.trim(),
        author: localStorage.getItem('username') || 'Onbekend',
      }),
    })

    if (!res.ok) {
      throw new Error('Kon nieuw topic niet aanmaken')
    }

    const data = await res.json()

    // nieuwe topic bovenaan lijst zetten
    const newItem: DiscussionListItem = {
      id: data.id,
      title: data.title,
      author: data.author,
      lastActivityAt: data.lastActivityAt,
      reactionsCount: data.reactionsCount,
    }
    discussions.value.unshift(newItem)

    closeModal()
  } catch (e) {
    formError.value = 'Er ging iets mis bij het aanmaken van het topic.'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="min-h-screen bg-[--color-background] text-white">
    <section class="max-w-5xl mx-auto px-6 py-20">
      <!-- Header -->
      <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-10">
        <div>
          <h1 class="text-4xl font-bold tracking-tight mb-2">Forum Discussies</h1>
          <p class="text-[--color-text-muted] max-w-xl">
            Praat mee over de verkiezingen, deel je mening en lees wat anderen belangrijk vinden.
          </p>
        </div>

        <button
          @click="openModal"
          class="px-7 py-3 rounded-xl font-semibold bg-gradient-to-r from-[#d82f4c] to-[#ef3054]
                 text-white shadow-[0_2px_10px_rgba(239,48,84,0.15)]
                 hover:shadow-[0_3px_15px_rgba(239,48,84,0.25)]
                 hover:scale-[1.03] transition-all duration-200 ease-out
                 border border-[rgba(255,255,255,0.08)] mt-2 md:mt-0"
        >
          <span class="tracking-wide">+ Nieuw topic starten</span>
        </button>
      </div>

      <!-- Status -->
      <div v-if="loading" class="text-center text-lg text-[--color-text-muted]">
        Discussies worden geladen…
      </div>
      <div v-else-if="error" class="text-center text-lg text-red-400">
        {{ error }}
      </div>

      <!-- Lijst met topics -->
      <div v-else>
        <div v-if="discussions.length === 0" class="text-center text-gray-400 mt-20 italic">
          Er zijn nog geen discussies gestart. Begin de eerste!
        </div>

        <div v-else class="grid gap-6">
          <div
            v-for="topic in discussions"
            :key="topic.id"
            @click="goToDetail(topic.id)"
            class="cursor-pointer bg-[#111830] border border-[rgba(255,255,255,0.05)]
                   rounded-2xl p-6 hover:shadow-[0_0_20px_rgba(239,48,84,0.2)]
                   hover:border-[#ef3054] transition-all duration-200"
          >
            <h2 class="text-xl font-semibold mb-2 text-white">
              {{ topic.title }}
            </h2>
            <p class="text-[--color-text-muted] text-sm mb-2">
              Geplaatst door
              <span class="text-[--color-primary] font-medium">{{ topic.author }}</span>
            </p>
            <div class="flex flex-wrap items-center gap-3 text-xs text-[--color-text-muted]">
              <span>Laatste activiteit: {{ new Date(topic.lastActivityAt).toLocaleString() }}</span>
              <span>•</span>
              <span>{{ topic.reactionsCount }} reacties</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Modal voor nieuw topic -->
    <teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 z-40 flex items-center justify-center bg-black/60"
      >
        <div
          class="bg-[#111830] rounded-2xl border border-[rgba(255,255,255,0.08)]
                 w-full max-w-lg p-6 shadow-2xl"
        >
          <h2 class="text-2xl font-semibold mb-4 text-white">Nieuw topic starten</h2>

          <form @submit.prevent="createDiscussion" class="flex flex-col gap-4">
            <div>
              <label class="block text-sm font-medium mb-1 text-[--color-text-muted]">
                Titel
              </label>
              <input
                v-model="formTitle"
                type="text"
                class="w-full rounded-lg bg-[#0B132B] border border-gray-700 px-3 py-2
                       text-white focus:outline-none focus:border-[--color-primary]"
                placeholder="Bijvoorbeeld: Wat vind jij van de verkiezingen in 2025?"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-1 text-[--color-text-muted]">
                Bericht
              </label>
              <textarea
                v-model="formBody"
                class="w-full rounded-lg bg-[#0B132B] border border-gray-700 px-3 py-2
                       text-white focus:outline-none focus:border-[--color-primary] min-h-[120px]
                       resize-none"
                placeholder="Schrijf hier jouw mening, vraag of stelling…"
              ></textarea>
            </div>

            <p v-if="formError" class="text-red-400 text-sm">
              {{ formError }}
            </p>

            <div class="flex justify-end gap-3 mt-2">
              <button
                type="button"
                @click="closeModal"
                class="px-4 py-2 rounded-lg border border-gray-600
                       text-[--color-text-muted] hover:bg-gray-800 transition"
              >
                Annuleren
              </button>

              <button
                type="submit"
                :disabled="submitting"
                class="px-6 py-2 rounded-xl font-semibold bg-gradient-to-r from-[#d82f4c] to-[#ef3054]
                       text-white shadow-[0_2px_10px_rgba(239,48,84,0.15)]
                       hover:shadow-[0_3px_15px_rgba(239,48,84,0.25)]
                       hover:scale-[1.03] transition-all duration-200 ease-out
                       border border-[rgba(255,255,255,0.08)]
                       disabled:opacity-60 disabled:cursor-not-allowed"
              >
                {{ submitting ? 'Plaatsen…' : 'Topic plaatsen' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </teleport>
  </main>
</template>
