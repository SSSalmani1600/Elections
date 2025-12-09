<script setup lang="ts">
import { useAuth } from '@/store/authStore'
import { ref, computed, onMounted } from 'vue'
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

const { user } = useAuth();

// Filter state
type FilterType = 'recent' | 'popular'
const activeFilter = ref<FilterType>('recent')

// Sorted discussions based on filter
const sortedDiscussions = computed(() => {
  const copy = [...discussions.value]
  if (activeFilter.value === 'popular') {
    return copy.sort((a, b) => b.reactionsCount - a.reactionsCount)
  } else {
    return copy.sort((a, b) => new Date(b.lastActivityAt).getTime() - new Date(a.lastActivityAt).getTime())
  }
})

// Modal state
const showModal = ref(false)
const submitting = ref(false)
const formTitle = ref('')
const formBody = ref('')
const formError = ref<string | null>(null)

// ---------------------- LOAD DISCUSSIONS ----------------------
onMounted(async () => {
  try {
    const res = await fetch('http://localhost:8080/api/discussions')
    if (!res.ok) {
      throw new Error('Kon discussies niet laden')
    }

    discussions.value = await res.json()
  } catch (e: unknown) {
    if (e instanceof Error) {
      error.value = e.message
    } else {
      error.value = 'Er ging iets mis bij het ophalen'
    }
  } finally {
    loading.value = false
  }
})
function openModal() {
  // Check of gebruiker is ingelogd
  if (!user.value) {
    router.push('/inloggen')
    return
  }
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

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('nl-NL', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// ---------------------- CREATE DISCUSSION ----------------------
async function createDiscussion() {
  const rawTitle = formTitle.value.trim();
  const rawBody = formBody.value.trim();

  if (!rawTitle || !rawBody) {
    formError.value = 'Titel en bericht mogen niet leeg zijn.';
    return;
  }

  submitting.value = true;
  formError.value = null;

  try {
    if (!user.value) {
      throw new Error("Je moet ingelogd zijn om een topic te starten")
    }


    const titleCheck = await moderateText(rawTitle);


    const bodyCheck = await moderateText(rawBody);


    if (titleCheck.isBlocked || bodyCheck.isBlocked) {
      throw new ModerationBlockError(
        "Bericht bevat schadelijke inhoud",
        titleCheck.isBlocked ? titleCheck : bodyCheck
      );
    }


    if (titleCheck.requiresConfirmation || bodyCheck.requiresConfirmation) {
      const warnings = [
        ...titleCheck.warnings,
        ...bodyCheck.warnings
      ].join("\n");

      if (!window.confirm(`WAARSCHUWING:\n${warnings}\nToch plaatsen?`)) {
        submitting.value = false;
        return;
      }
    }


    if (titleCheck.isFlagged || bodyCheck.isFlagged) {
      alert("Ongepaste taal is automatisch gefilterd.");
    }

    const res = await fetch('http://localhost:8080/api/discussions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: "include", // 🟢 BELANGRIJK!
      body: JSON.stringify({
        title: titleCheck.moderatedText,
        body: bodyCheck.moderatedText,
        category: 'algemeen',
        userId: user.value.id,
      }),
    });



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
      reactionsCount: data.reactionsCount,
    })

    closeModal()

  } catch (e: unknown) {

    if (e instanceof ModerationBlockError) {
      // Zware Toxiciteit (403): Geblokkeerd door de AI-check
      const blockedResult = e.moderationResult;
      formError.value = `Topic geblokkeerd: ${blockedResult.warnings.join(" ")}`;
    } else if (e instanceof Error) {
      formError.value = e.message
    } else {
      formError.value = 'Er ging iets mis bij het aanmaken van het topic'
    }
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="min-h-screen bg-[--color-background] text-white">
    <div class="max-w-5xl mx-auto px-6 py-16">

      <div class="text-center mb-12">
        <div
          class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-gradient-to-br from-[#ef3054] to-[#d82f4c] mb-6 shadow-lg shadow-[#ef3054]/20">
          <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z" />
          </svg>
        </div>
        <h1 class="text-4xl font-bold tracking-tight mb-2">Forum Discussies</h1>
        <p class="text-gray-400 text-lg max-w-xl mx-auto">
          Praat mee over de verkiezingen, deel je mening en lees wat anderen belangrijk vinden.
        </p>
      </div>

      <div class="flex flex-col sm:flex-row items-center justify-between gap-4 mb-8">
        <div class="flex items-center gap-2 p-1.5 bg-[#111830]/80 border border-white/10 rounded-xl">
          <button @click="activeFilter = 'recent'" :class="[
            'flex items-center gap-2 px-4 py-2.5 rounded-lg font-medium text-sm transition-all',
            activeFilter === 'recent'
              ? 'bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white shadow-lg'
              : 'text-gray-400 hover:text-white hover:bg-white/5'
          ]">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            Recent
          </button>
          <button @click="activeFilter = 'popular'" :class="[
            'flex items-center gap-2 px-4 py-2.5 rounded-lg font-medium text-sm transition-all',
            activeFilter === 'popular'
              ? 'bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white shadow-lg'
              : 'text-gray-400 hover:text-white hover:bg-white/5'
          ]">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M9.879 16.121A3 3 0 1012.015 11L11 14H9c0 .768.293 1.536.879 2.121z" />
            </svg>
            Populair
          </button>
        </div>

        <button @click="openModal" class="px-6 py-3 rounded-xl font-semibold bg-gradient-to-r from-[#ef3054] to-[#d82f4c]
                 text-white shadow-lg shadow-[#ef3054]/20
                 hover:shadow-xl hover:shadow-[#ef3054]/30
                 hover:scale-[1.02] transition-all duration-300
                 flex items-center gap-2 text-sm">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          Nieuw topic
        </button>
      </div>

      <div v-if="loading" class="flex justify-center">
        <div class="bg-[#111830]/80 border border-white/10 rounded-2xl p-10 text-center">
          <div class="animate-spin w-12 h-12 border-4 border-[#ef3054] border-t-transparent rounded-full mx-auto mb-4">
          </div>
          <p class="text-gray-400">Discussies laden...</p>
        </div>
      </div>

      <div v-else-if="error" class="flex justify-center">
        <div class="bg-red-900/20 border border-red-500/30 rounded-2xl p-8 text-center max-w-md">
          <svg class="w-16 h-16 text-red-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <p class="text-red-200 text-lg">{{ error }}</p>
        </div>
      </div>

      <div v-else-if="sortedDiscussions.length === 0" class="flex justify-center">
        <div class="bg-[#111830]/80 border border-white/10 rounded-2xl p-12 text-center max-w-md">
          <svg class="w-20 h-20 text-gray-600 mx-auto mb-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
          </svg>
          <h3 class="text-xl font-semibold mb-2">Nog geen discussies</h3>
          <p class="text-gray-500 mb-6">Wees de eerste om een gesprek te starten!</p>
          <button @click="openModal"
                  class="px-6 py-3 rounded-xl font-medium bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white hover:scale-[1.02] transition-all">
            Start eerste topic
          </button>
        </div>
      </div>

      <div v-else class="space-y-4">
        <div v-for="topic in sortedDiscussions" :key="topic.id" @click="goToDetail(topic.id)" class="group cursor-pointer bg-[#111830]/80 border border-white/10 rounded-2xl p-6
                 hover:border-[#ef3054]/50 hover:shadow-lg hover:shadow-[#ef3054]/10
                 transition-all duration-300">
          <div class="flex items-start gap-4">
            <div
              class="w-12 h-12 rounded-full bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center text-lg font-bold flex-shrink-0 shadow-lg">
              {{ topic.author.charAt(0).toUpperCase() }}
            </div>

            <div class="flex-1 min-w-0">
              <h2 class="text-lg font-semibold text-white group-hover:text-[#ef3054] transition-colors truncate">
                {{ topic.title }}
              </h2>

              <div class="flex items-center gap-2 mt-1 text-sm text-gray-400">
                <span class="text-[#ef3054] font-medium">{{ topic.author }}</span>
                <span>•</span>
                <span>{{ formatDate(topic.lastActivityAt) }}</span>
              </div>
            </div>

            <div class="flex items-center gap-2 px-4 py-2 rounded-xl bg-white/5 border border-white/5 flex-shrink-0">
              <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
              <span class="text-sm font-medium text-gray-400">{{ topic.reactionsCount }}</span>
            </div>

            <svg
              class="w-5 h-5 text-gray-600 group-hover:text-[#ef3054] group-hover:translate-x-1 transition-all flex-shrink-0"
              fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-black/70 backdrop-blur-sm" @click="closeModal"></div>

        <div class="relative bg-[#111830] border border-white/10 rounded-2xl w-full max-w-lg shadow-2xl">
          <div class="p-6 border-b border-white/5">
            <div class="flex items-center gap-4">
              <div
                class="w-12 h-12 rounded-xl bg-gradient-to-br from-[#ef3054] to-[#d82f4c] flex items-center justify-center shadow-lg">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
              </div>
              <div>
                <h2 class="text-xl font-bold text-white">Nieuw topic starten</h2>
                <p class="text-gray-400 text-sm">Deel je gedachten met de community</p>
              </div>
            </div>
          </div>

          <form @submit.prevent="createDiscussion" class="p-6 space-y-5">
            <div>
              <label class="flex items-center gap-2 text-sm font-medium text-gray-300 mb-2">
                <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                </svg>
                Titel
              </label>
              <input v-model="formTitle" type="text" placeholder="Waar wil je het over hebben?"
                     class="w-full p-4 rounded-xl bg-[#0B132B] border border-white/10 focus:border-[#ef3054] focus:ring-2 focus:ring-[#ef3054]/20 focus:outline-none text-white placeholder-gray-500 transition-all" />
            </div>

            <div>
              <label class="flex items-center gap-2 text-sm font-medium text-gray-300 mb-2">
                <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h7" />
                </svg>
                Bericht
              </label>
              <textarea v-model="formBody" placeholder="Schrijf je bericht..." rows="5"
                        class="w-full p-4 rounded-xl bg-[#0B132B] border border-white/10 focus:border-[#ef3054] focus:ring-2 focus:ring-[#ef3054]/20 focus:outline-none text-white placeholder-gray-500 resize-none transition-all"></textarea>
            </div>

            <div v-if="formError"
                 class="flex items-center gap-3 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-200 text-sm">
              <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {{ formError }}
            </div>

            <div class="flex gap-3 pt-2">
              <button type="button" @click="closeModal"
                      class="flex-1 px-5 py-3 rounded-xl font-medium bg-white/5 border border-white/10 hover:bg-white/10 transition-all text-gray-300">
                Annuleren
              </button>
              <button type="submit" :disabled="submitting" class="flex-1 px-5 py-3 rounded-xl font-medium bg-gradient-to-r from-[#ef3054] to-[#d82f4c] text-white
                       shadow-lg shadow-[#ef3054]/20 hover:shadow-xl hover:shadow-[#ef3054]/30
                       transition-all disabled:opacity-50 disabled:cursor-not-allowed
                       flex items-center justify-center gap-2">
                <span v-if="submitting"
                      class="animate-spin w-5 h-5 border-2 border-white border-t-transparent rounded-full"></span>
                <span v-else>
                  <svg class="w-5 h-5 inline mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                  </svg>
                  Plaatsen
                </span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </teleport>
  </main>
</template>
