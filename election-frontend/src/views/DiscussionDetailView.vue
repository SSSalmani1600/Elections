<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

type Reaction = {
  id: number
  userId: number
  author: string
  message: string
  createdAt: string
}

type DiscussionDetail = {
  id: string
  title: string
  author: string
  body: string
  createdAt: string
  lastActivityAt: string
  reactionsCount: number
  reactions: Reaction[]
}

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const error = ref<string | null>(null)
const discussion = ref<DiscussionDetail | null>(null)

// Current user
const currentUserId = computed(() => {
  const id = localStorage.getItem('userId')
  console.log('Current userId from localStorage:', id)
  return id ? Number(id) : null
})

// Helper function to check if reaction belongs to current user
function isOwnReaction(reaction: Reaction): boolean {
  const result = currentUserId.value !== null && reaction.userId === currentUserId.value
  console.log(`Reaction ${reaction.id} - userId: ${reaction.userId}, currentUserId: ${currentUserId.value}, isOwn: ${result}`)
  return result
}

// Edit state
const editingReactionId = ref<number | null>(null)
const editingMessage = ref('')

// Delete confirmation state
const deletingReactionId = ref<number | null>(null)

onMounted(async () => {
  const id = route.params.id as string
  try {
    const res = await fetch(`http://localhost:8080/api/discussions/${id}`)
    if (!res.ok) {
      if (res.status === 404) throw new Error('Discussie niet gevonden')
      throw new Error('Er ging iets mis bij het ophalen')
    }
    discussion.value = await res.json()
  } catch (e: any) {
    error.value = e.message ?? 'Onbekende fout'
  } finally {
    loading.value = false
  }
})

function backToList() {
  router.push({ name: 'forum' })
}

const newReaction = ref('')
const submitting = ref(false)
const errorReaction = ref('')

async function postReaction() {
  if (!newReaction.value.trim()) {
    errorReaction.value = 'Reactie mag niet leeg zijn.'
    return
  }

  submitting.value = true
  errorReaction.value = ''
  const id = route.params.id as string

  try {
    const res = await fetch(`http://localhost:8080/api/discussions/${id}/reactions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        message: newReaction.value,
        userId: Number(localStorage.getItem('userId') ?? 1),
      }),
    })

    if (!res.ok) throw new Error('Fout bij plaatsen reactie')
    const reaction = await res.json()

    if (discussion.value) {
      discussion.value.reactions.push(reaction)
      discussion.value.reactionsCount++
    }

    localStorage.setItem('forumRefresh', Date.now().toString())
    newReaction.value = ''
  } catch (e) {
    errorReaction.value = 'Er ging iets mis bij het plaatsen van je reactie.'
  } finally {
    submitting.value = false
  }
}

// ---------------------- EDIT REACTION ----------------------
function startEdit(reaction: Reaction) {
  editingReactionId.value = reaction.id
  editingMessage.value = reaction.message
}

function cancelEdit() {
  editingReactionId.value = null
  editingMessage.value = ''
}

async function saveEdit(reactionId: number) {
  if (!editingMessage.value.trim()) return

  try {
    const res = await fetch(`http://localhost:8080/api/discussions/reactions/${reactionId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        message: editingMessage.value,
        userId: currentUserId.value,
      }),
    })

    if (!res.ok) {
      const errorText = await res.text()
      throw new Error(errorText || 'Fout bij bewerken')
    }

    const updated = await res.json()

    // Update in local state
    if (discussion.value) {
      const index = discussion.value.reactions.findIndex(r => r.id === reactionId)
      if (index !== -1) {
        discussion.value.reactions[index] = updated
      }
    }

    cancelEdit()
  } catch (e: any) {
    errorReaction.value = e.message || 'Er ging iets mis bij het bewerken.'
  }
}

// ---------------------- DELETE REACTION ----------------------
function confirmDelete(reactionId: number) {
  deletingReactionId.value = reactionId
}

function cancelDelete() {
  deletingReactionId.value = null
}

async function deleteReaction(reactionId: number) {
  try {
    const res = await fetch(`http://localhost:8080/api/discussions/reactions/${reactionId}`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: currentUserId.value,
      }),
    })

    if (!res.ok) {
      const errorText = await res.text()
      throw new Error(errorText || 'Fout bij verwijderen')
    }

    // Remove from local state
    if (discussion.value) {
      discussion.value.reactions = discussion.value.reactions.filter(r => r.id !== reactionId)
      discussion.value.reactionsCount = Math.max(0, discussion.value.reactionsCount - 1)
    }

    cancelDelete()
  } catch (e: any) {
    errorReaction.value = e.message || 'Er ging iets mis bij het verwijderen.'
  }
}
</script>

<template>
  <main class="min-h-screen bg-[--color-background] text-white">
    <section class="max-w-4xl mx-auto px-6 py-20">
      <button
        @click="backToList"
        class="flex items-center gap-2 mb-10 text-[--color-primary] font-medium hover:underline"
      >
        ← Terug naar forum
      </button>

      <div v-if="loading" class="text-center text-lg text-gray-400">Even geduld…</div>
      <div v-else-if="error" class="text-center text-lg text-red-400">{{ error }}</div>

      <div
        v-else-if="discussion"
        class="rounded-2xl p-10 border border-[rgba(255,255,255,0.08)]
               bg-gradient-to-br from-[#0B132B]/90 to-[#111830]/90
               shadow-[0_0_25px_rgba(0,0,0,0.3)] backdrop-blur-md"
      >
        <h1 class="text-4xl font-bold mb-4 tracking-tight text-white">{{ discussion.title }}</h1>

        <div class="flex flex-wrap items-center gap-3 text-sm text-gray-400 mb-8">
          <span class="text-[--color-primary] font-medium">{{ discussion.author }}</span>
          <span>•</span>
          <span>{{ new Date(discussion.lastActivityAt).toLocaleString() }}</span>
          <span>•</span>
          <span>{{ discussion.reactionsCount }} reacties</span>
        </div>

        <article
          class="bg-[rgba(255,255,255,0.03)] border border-[rgba(255,255,255,0.06)]
                 p-6 rounded-xl text-[--color-text-base] leading-relaxed mb-10"
        >
          <p class="whitespace-pre-line text-lg">{{ discussion.body }}</p>
        </article>

        <div>
          <h2 class="text-2xl font-semibold mb-4 text-white">Reacties</h2>

          <!-- reactieformulier -->
          <form @submit.prevent="postReaction" class="flex flex-col gap-3 mb-8">
            <textarea
              v-model="newReaction"
              placeholder="Schrijf hier je reactie..."
              class="p-3 rounded-xl bg-[#0B132B] text-white border border-gray-700
                     resize-none min-h-[100px] focus:outline-none focus:border-[#ef3054]"
            ></textarea>

            <button
              type="submit"
              :disabled="submitting"
              class="self-start px-7 py-3 rounded-xl font-semibold
                     bg-gradient-to-r from-[#d82f4c] to-[#ef3054]
                     text-white shadow-[0_2px_10px_rgba(239,48,84,0.15)]
                     hover:shadow-[0_3px_15px_rgba(239,48,84,0.25)]
                     hover:scale-[1.03] transition-all duration-200 ease-out
                     border border-[rgba(255,255,255,0.08)] mt-2
                     disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ submitting ? 'Plaatsen…' : 'Plaatsen' }}
            </button>

            <p v-if="errorReaction" class="text-red-400">{{ errorReaction }}</p>
          </form>

          <!-- Geen reacties -->
          <div v-if="discussion.reactions.length === 0" class="text-gray-400 italic">
            Nog geen reacties — wees de eerste om iets te zeggen!
          </div>

          <!-- Reactielijst (scrollbaar) -->
          <div
            v-else
            class="space-y-4 p-6 rounded-xl border border-[rgba(255,255,255,0.05)]
                   bg-[rgba(255,255,255,0.02)] max-h-[400px] overflow-y-auto
                   scrollbar-thin scrollbar-thumb-[#ef3054]/60 scrollbar-track-transparent"
          >
            <div
              v-for="r in discussion.reactions"
              :key="r.id"
              class="bg-[#0B132B]/80 border border-gray-700 rounded-xl p-5
                     text-white transition hover:border-[#ef3054] relative"
            >
              <!-- Delete Confirmation Modal -->
              <div v-if="deletingReactionId === r.id" 
                   class="absolute inset-0 bg-black/80 rounded-xl flex items-center justify-center z-10">
                <div class="text-center p-4">
                  <p class="text-white mb-4">Weet je zeker dat je deze reactie wilt verwijderen?</p>
                  <div class="flex gap-3 justify-center">
                    <button 
                      @click="cancelDelete"
                      class="px-4 py-2 rounded-lg border border-gray-600 text-gray-300 hover:bg-gray-800"
                    >
                      Annuleren
                    </button>
                    <button 
                      @click="deleteReaction(r.id)"
                      class="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700"
                    >
                      Verwijderen
                    </button>
                  </div>
                </div>
              </div>

              <!-- Header with author and actions -->
              <div class="flex justify-between items-start mb-2">
                <p class="text-sm text-gray-400">
                  {{ r.author }} · {{ new Date(r.createdAt).toLocaleString() }}
                </p>
                
                <!-- Edit/Delete buttons (only for own reactions) -->
                <div v-if="isOwnReaction(r) && editingReactionId !== r.id" class="flex gap-2">
                  <button 
                    @click="startEdit(r)"
                    class="p-1.5 rounded-lg text-gray-400 hover:text-blue-400 hover:bg-blue-400/10 transition"
                    title="Bewerken"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                            d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </button>
                  <button 
                    @click="confirmDelete(r.id)"
                    class="p-1.5 rounded-lg text-gray-400 hover:text-red-400 hover:bg-red-400/10 transition"
                    title="Verwijderen"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>

              <!-- Edit mode -->
              <div v-if="editingReactionId === r.id" class="mt-2">
                <textarea
                  v-model="editingMessage"
                  class="w-full p-3 rounded-lg bg-[#0B132B] text-white border border-gray-600 
                         resize-none min-h-[80px] focus:outline-none focus:border-[#ef3054]"
                ></textarea>
                <div class="flex gap-2 mt-2">
                  <button 
                    @click="cancelEdit"
                    class="px-4 py-2 rounded-lg border border-gray-600 text-gray-300 hover:bg-gray-800 text-sm"
                  >
                    Annuleren
                  </button>
                  <button 
                    @click="saveEdit(r.id)"
                    class="px-4 py-2 rounded-lg bg-gradient-to-r from-[#d82f4c] to-[#ef3054] text-white text-sm"
                  >
                    Opslaan
                  </button>
                </div>
              </div>

              <!-- Normal view -->
              <p v-else class="text-base leading-relaxed">{{ r.message }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
textarea::placeholder {
  color: rgba(255, 255, 255, 0.5);
}
.scrollbar-thin::-webkit-scrollbar {
  width: 8px;
}
.scrollbar-thin::-webkit-scrollbar-thumb {
  background-color: rgba(239, 48, 84, 0.6);
  border-radius: 9999px;
}
.scrollbar-thin::-webkit-scrollbar-track {
  background: transparent;
}
</style>
