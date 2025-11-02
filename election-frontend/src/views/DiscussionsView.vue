<script setup lang="ts">
import { ref, onMounted, computed, nextTick, watch, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

// ✅ Toast state
const showToast = ref(false)
const toastMessage = ref('')

// Type voor 1 discussie
type Discussion = {
  id: string
  title: string
  author: string
  reactionsCount: number
  lastActivityAt: string
}

// Variabelen voor data en status
const discussions = ref<Discussion[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const router = useRouter()

// Data ophalen bij laden
onMounted(async () => {
  try {
    const res = await fetch('http://localhost:8080/api/discussions')
    if (!res.ok) throw new Error('Kon discussies niet laden')
    discussions.value = await res.json()
  } catch (e: unknown) {
    if (e instanceof Error) error.value = e.message
    else error.value = 'Onbekende fout'
  } finally {
    loading.value = false
  }
})

// Chronologisch sorteren
const sorted = computed(() =>
  [...discussions.value].sort(
    (a, b) =>
      new Date(b.lastActivityAt ?? '').getTime() -
      new Date(a.lastActivityAt ?? '').getTime()
  )
)

// Klik naar detailpagina
function openDetail(id: string) {
  router.push({ name: 'discussion-detail', params: { id } })
}

// Nieuw topic aanmaken (modal + form)
const showModal = ref(false)
const form = ref({ title: '', body: '' })
const titleError = ref<string | null>(null)
const bodyError = ref<string | null>(null)
const submitting = ref(false)

const canSubmit = computed(() => {
  return !!form.value.title?.trim() && !!form.value.body?.trim() && !submitting.value
})

// Focus op titel bij openen van modal
const titleInputRef = ref<HTMLInputElement | null>(null)

watch(showModal, async (open) => {
  if (open) {
    titleError.value = null
    bodyError.value = null
    await nextTick()
    titleInputRef.value?.focus()
  }
})

// ESC toets sluit modal
const onKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape' && showModal.value && !submitting.value) closeModal()
}
onMounted(() => window.addEventListener('keydown', onKeydown))
onBeforeUnmount(() => window.removeEventListener('keydown', onKeydown))

// Validatie
function validate() {
  titleError.value = null
  bodyError.value = null

  const t = form.value.title?.trim() ?? ''
  const b = form.value.body?.trim() ?? ''

  if (t.length < 5 || t.length > 120) {
    titleError.value = 'Titel moet tussen 5 en 120 tekens zijn.'
  }
  if (b.length < 10 || b.length > 2000) {
    bodyError.value = 'Bericht moet tussen 10 en 2000 tekens zijn.'
  }
  return !titleError.value && !bodyError.value
}

function closeModal() {
  showModal.value = false
  form.value = { title: '', body: '' }
}

// Nieuw topic opslaan + toast tonen
async function createDiscussion() {
  if (!validate()) return
  submitting.value = true

  try {
    const username = localStorage.getItem('username') || 'Onbekende gebruiker'

    const res = await fetch('http://localhost:8080/api/discussions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + (localStorage.getItem('JWT') || '')
      },
      body: JSON.stringify({
        title: form.value.title.trim(),
        body: form.value.body.trim(),
        author: username
      })
    })

    if (!res.ok) throw new Error(`Server gaf status ${res.status}`)

    const newDiscussion = await res.json()
    discussions.value.unshift(newDiscussion)
    closeModal()

    // ✅ Toon toast
    toastMessage.value = 'Topic succesvol geplaatst!'
    showToast.value = true
    console.log('TOAST ACTIEF ✅') // debug
    setTimeout(() => (showToast.value = false), 3000)
  } catch (err) {
    console.error('❌ Fout bij aanmaken topic:', err)
    alert('Er ging iets mis bij het plaatsen van je topic.')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="relative flex flex-col items-center bg-[--color-background] text-[--color-text-base] min-h-screen">
    <!-- ✅ Toast melding -->
    <transition name="fade">
      <div
        v-if="showToast"
        class="absolute top-8 left-1/2 -translate-x-1/2 bg-[rgba(239,48,84,0.95)]
               text-white px-6 py-3 rounded-xl shadow-2xl z-[9999] text-lg font-semibold"
      >
        ✅ {{ toastMessage }}
      </div>
    </transition>

    <div class="flex flex-col items-center w-full mt-[75px] gap-8 px-6 max-w-5xl">
      <div class="flex flex-col items-center gap-3 text-center">
        <h1 class="text-3xl font-bold">Forum Discussies</h1>
        <p class="text-[--color-text-muted] text-lg max-w-[680px]">
          <strong>Lees</strong> mee met de gesprekken,
          <strong>deel</strong> je mening en
          <strong>ontdek</strong> wat anderen denken over de verkiezingen.
        </p>
      </div>

      <button
        @click="showModal = true"
        class="px-7 py-3 rounded-xl font-semibold
         bg-gradient-to-r from-[#d82f4c] to-[#ef3054]
         text-white
         shadow-[0_2px_10px_rgba(239,48,84,0.15)]
         hover:shadow-[0_3px_15px_rgba(239,48,84,0.25)]
         hover:scale-[1.03]
         transition-all duration-200 ease-out
         border border-[rgba(255,255,255,0.08)]
         mt-2"
      >
        <span class="tracking-wide">+ Nieuw topic starten</span>
      </button>

      <div class="flex flex-col w-full gap-6 mt-6">
        <div v-if="loading" class="text-lg text-center">Laden...</div>
        <div v-else-if="error" class="text-red-400 text-center">{{ error }}</div>

        <div
          v-else
          v-for="d in sorted"
          :key="d.id"
          @click="openDetail(d.id)"
          class="p-6 rounded-2xl shadow-md hover:shadow-lg transition cursor-pointer
                 bg-[color:rgb(35,45,80)]
                 hover:bg-[color:rgb(45,55,95)]
                 border border-[color:rgba(255,255,255,0.06)]"
        >
          <h2 class="text-xl font-semibold text-[--color-primary] mb-2">{{ d.title }}</h2>
          <p class="text-sm text-[--color-text-muted]">
            door <span class="font-medium">{{ d.author }}</span> • {{ d.reactionsCount }} reacties
          </p>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black/70 flex items-center justify-center z-50" @click.self="!submitting && closeModal()">
      <div class="bg-[#111830] p-8 rounded-2xl w-[500px] max-w-[90%] text-white shadow-xl border border-gray-700">
        <h2 class="text-2xl font-bold mb-4 text-[--color-primary] text-center">Nieuw topic starten</h2>

        <form @submit.prevent="createDiscussion" class="flex flex-col gap-4">
          <div>
            <label class="text-sm mb-1 block">Titel</label>
            <input
              ref="titleInputRef"
              v-model="form.title"
              type="text"
              class="w-full bg-[#1b2144] border rounded-lg p-2 text-white"
              placeholder="Bijv. Wat vind jij van de verkiezingen?"
            />
            <p v-if="titleError" class="text-red-400 text-sm mt-1">{{ titleError }}</p>
          </div>

          <div>
            <label class="text-sm mb-1 block">Bericht</label>
            <textarea
              v-model="form.body"
              rows="5"
              class="w-full bg-[#1b2144] border rounded-lg p-2 text-white resize-none"
              placeholder="Schrijf hier je mening of vraag..."
            ></textarea>
            <p v-if="bodyError" class="text-red-400 text-sm mt-1">{{ bodyError }}</p>
          </div>

          <div class="flex justify-end gap-3 mt-4">
            <button type="button" @click="closeModal" :disabled="submitting" class="px-4 py-2 rounded-lg bg-gray-600 text-white hover:opacity-80 disabled:opacity-50">
              Annuleren
            </button>
            <button type="submit" :disabled="!canSubmit" class="px-4 py-2 rounded-lg bg-[--color-primary] text-white hover:opacity-90 disabled:opacity-50">
              {{ submitting ? 'Plaatsen...' : 'Plaatsen' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
