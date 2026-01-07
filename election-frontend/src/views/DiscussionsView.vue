<script setup lang="ts">
import { useAuth } from '@/store/authStore'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { moderateText, ModerationBlockError } from '@/services/ModerationService.ts'
import { backendAPI } from '@/apiClient'

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

const { user } = useAuth()

/* ----------------------------------------------
   FILTER
------------------------------------------------*/
type FilterType = 'recent' | 'popular'
const activeFilter = ref<FilterType>('recent')

const sortedDiscussions = computed(() => {
  const items = [...discussions.value]
  return activeFilter.value === 'popular'
    ? items.sort((a, b) => b.reactionsCount - a.reactionsCount)
    : items.sort(
      (a, b) =>
        new Date(b.lastActivityAt).getTime() - new Date(a.lastActivityAt).getTime()
    )
})

/* ----------------------------------------------
   LOAD DISCUSSIONS
------------------------------------------------*/
onMounted(async () => {
  try {
    const res = await fetch(`${backendAPI}/api/discussions`)
    if (!res.ok) throw new Error('Kon discussies niet laden')
    discussions.value = await res.json()
  } catch (e: any) {
    error.value = e.message ?? 'Er ging iets mis'
  } finally {
    loading.value = false
  }
})

/* ----------------------------------------------
   MODAL
------------------------------------------------*/
const showModal = ref(false)
const submitting = ref(false)
const formTitle = ref('')
const formBody = ref('')
const formError = ref<string | null>(null)

function openModal() {
  if (!user.value) return router.push('/inloggen')
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

/* ----------------------------------------------
   DATE FORMAT
------------------------------------------------*/
function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('nl-NL', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/* ----------------------------------------------
   LOADBAR (supersnel zichtbaar)
------------------------------------------------*/
const showLoadbar = ref(false)
const loadbarProgress = ref(0)
let loadbarTimer: any = null

function startLoadbar() {
  showLoadbar.value = true
  loadbarProgress.value = 5

  loadbarTimer = setInterval(() => {
    if (loadbarProgress.value < 90) {
      loadbarProgress.value += Math.random() * 8
    }
  }, 200)
}

function finishLoadbar() {
  loadbarProgress.value = 100
  setTimeout(() => {
    showLoadbar.value = false
    loadbarProgress.value = 0
    clearInterval(loadbarTimer)
  }, 400)
}

/* ----------------------------------------------
   CREATE DISCUSSION
------------------------------------------------*/
async function createDiscussion() {
  const rawTitle = formTitle.value.trim()
  const rawBody = formBody.value.trim()

  if (!rawTitle || !rawBody) {
    formError.value = 'Titel en bericht mogen niet leeg zijn.'
    return
  }

  submitting.value = true
  startLoadbar()
  formError.value = null

  try {
    if (!user.value) throw new Error('Je moet ingelogd zijn om een topic te starten')

    // AI Moderation
    const titleCheck = await moderateText(rawTitle)
    const bodyCheck = await moderateText(rawBody)

    if (titleCheck.isBlocked || bodyCheck.isBlocked) {
      throw new ModerationBlockError(
        'Bericht bevat schadelijke inhoud',
        titleCheck.isBlocked ? titleCheck : bodyCheck
      )
    }

    if (titleCheck.requiresConfirmation || bodyCheck.requiresConfirmation) {
      const warnings = [...titleCheck.warnings, ...bodyCheck.warnings].join('\n')
      if (!window.confirm(`WAARSCHUWING:\n${warnings}\nToch plaatsen?`)) {
        submitting.value = false
        finishLoadbar()
        return
      }
    }

    if (titleCheck.isFlagged || bodyCheck.isFlagged) {
      alert('Ongepaste taal is automatisch gefilterd.')
    }

    // API CALL
    const res = await fetch(`${backendAPI}/api/discussions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({
        title: titleCheck.moderatedText,
        body: bodyCheck.moderatedText,
        category: 'algemeen',
        userId: user.value.id
      })
    })

    if (!res.ok) {
      const text = await res.text().catch(() => '')
      throw new Error(text || `Kon topic niet maken (status ${res.status})`)
    }

    const data = await res.json()

    discussions.value.unshift({
      id: data.id,
      title: data.title,
      author: data.author,
      lastActivityAt: data.lastActivityAt,
      reactionsCount: data.reactionsCount
    })

    closeModal()
  } catch (e: any) {
    if (e instanceof ModerationBlockError) {
      formError.value = `Topic geblokkeerd: ${e.moderationResult.warnings.join(' ')}`
    } else {
      formError.value = e.message ?? 'Er ging iets mis'
    }
  } finally {
    submitting.value = false
    finishLoadbar()
  }
}
</script>




<template>
  <main class="min-h-screen bg-[--color-background] text-white">

    <!-- 🔥 DUDELIJK ZICHTBARE LOADBAR -->
    <div v-if="showLoadbar" class="fixed top-0 left-0 w-full h-2 bg-black/40 backdrop-blur z-50">
      <div
        class="h-full bg-[#ef3054] shadow-[0_0_10px_rgba(239,48,84,0.8)] transition-all duration-150"
        :style="{ width: loadbarProgress + '%' }"
      ></div>
    </div>

    <div class="max-w-5xl mx-auto px-6 py-16">

      <!-- HEADER -->
      <div class="text-center mb-12">
        <div class="inline-flex justify-center items-center w-20 h-20 bg-gradient-to-br from-[#ef3054] to-[#d82f4c] rounded-full shadow-lg mb-6">
          <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z" />
          </svg>
        </div>

        <h1 class="text-4xl font-bold mb-2">Forum Discussies</h1>
        <p class="text-gray-400 max-w-xl mx-auto">Praat mee over de verkiezingen, deel je mening en lees wat anderen belangrijk vinden.</p>
      </div>

      <!-- FILTER -->
      <div class="flex flex-col sm:flex-row items-center justify-between gap-4 mb-8">
        <div class="flex gap-2 p-1.5 bg-[#111830]/80 rounded-xl border border-white/10">
          <button
            @click="activeFilter = 'recent'"
            :class="[
              'px-4 py-2.5 rounded-lg font-medium text-sm transition-all',
              activeFilter === 'recent'
                ? 'bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white shadow-lg'
                : 'text-gray-400 hover:text-white hover:bg-white/5'
            ]"
          >
            Recent
          </button>

          <button
            @click="activeFilter = 'popular'"
            :class="[
              'px-4 py-2.5 rounded-lg font-medium text-sm transition-all',
              activeFilter === 'popular'
                ? 'bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white shadow-lg'
                : 'text-gray-400 hover:text-white hover:bg-white/5'
            ]"
          >
            Populair
          </button>
        </div>

        <button
          @click="openModal"
          class="px-6 py-3 rounded-xl bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white shadow-lg hover:scale-[1.02] transition-all flex items-center gap-2"
        >
          Nieuw topic
        </button>
      </div>

      <!-- LOADING -->
      <div v-if="loading" class="text-center">
        <div class="bg-[#111830]/80 border border-white/10 rounded-2xl p-10 inline-block">
          <div class="animate-spin w-12 h-12 border-4 border-[#ef3054] border-t-transparent rounded-full mx-auto mb-4"></div>
          <p class="text-gray-400">Discussies laden...</p>
        </div>
      </div>

      <!-- ERROR -->
      <div v-else-if="error" class="text-center">
        <div class="bg-red-900/20 border border-red-500/30 rounded-2xl p-8 max-w-md mx-auto">
          <p class="text-red-300">{{ error }}</p>
        </div>
      </div>

      <!-- EMPTY -->
      <div v-else-if="sortedDiscussions.length === 0" class="text-center">
        <p class="text-gray-400">Nog geen discussies.</p>
      </div>

      <!-- LIST -->
      <div v-else class="space-y-4">
        <div
          v-for="topic in sortedDiscussions"
          :key="topic.id"
          @click="goToDetail(topic.id)"
          class="p-6 bg-[#111830]/80 border border-white/10 rounded-2xl cursor-pointer hover:border-[#ef3054]/50 transition-all"
        >
          <div class="flex justify-between items-start">
            <div>
              <h2 class="text-lg font-semibold">{{ topic.title }}</h2>
              <p class="text-gray-400 text-sm">{{ formatDate(topic.lastActivityAt) }}</p>
            </div>
            <div class="flex items-center gap-1.5 text-gray-400 text-sm">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              <span>{{ topic.reactionsCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL -->
    <teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-black/70 backdrop-blur-sm" @click="closeModal"></div>

        <div class="relative bg-[#111830] border border-white/10 rounded-2xl w-full max-w-lg shadow-2xl">

          <form @submit.prevent="createDiscussion" class="p-6 space-y-5">

            <h2 class="text-xl font-bold mb-4">Nieuw topic starten</h2>

            <!-- TITLE -->
            <div>
              <label class="block text-sm mb-1">Titel</label>
              <input
                v-model="formTitle"
                class="w-full p-4 rounded-xl bg-[#0B132B] border border-white/10 text-white"
                placeholder="Waar wil je het over hebben?"
              />
            </div>

            <!-- BODY -->
            <div>
              <label class="block text-sm mb-1">Bericht</label>
              <textarea
                v-model="formBody"
                rows="5"
                class="w-full p-4 rounded-xl bg-[#0B132B] border border-white/10 text-white resize-none"
                placeholder="Schrijf je bericht..."
              ></textarea>
            </div>

            <!-- ERROR -->
            <div v-if="formError" class="p-4 bg-red-500/10 border border-red-500/30 text-red-300 rounded-xl">
              {{ formError }}
            </div>

            <!-- BUTTONS -->
            <div class="flex gap-3">
              <button type="button" @click="closeModal"
                      class="flex-1 px-5 py-3 rounded-xl bg-white/5 border border-white/10 text-gray-300">
                Annuleren
              </button>

              <button type="submit" :disabled="submitting"
                      class="flex-1 px-5 py-3 rounded-xl bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white flex justify-center items-center gap-2 disabled:opacity-50">
                <span v-if="submitting"
                      class="animate-spin w-6 h-6 border-4 border-white border-t-transparent rounded-full"></span>
                <span v-else>Plaatsen</span>
              </button>
            </div>

          </form>

        </div>
      </div>
    </teleport>

  </main>
</template>
