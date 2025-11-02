<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

// ✅ Type voor 1 discussie
type Discussion = {
  id: string
  title: string
  author: string
  reactionsCount: number
  lastActivityAt: string
}

// ✅ Variabelen voor data en status
const discussions = ref<Discussion[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const router = useRouter()

// ✅ Data ophalen bij laden
onMounted(async () => {
  try {
    const res = await fetch('http://localhost:8080/api/discussions')
    if (!res.ok) throw new Error('Kon discussies niet laden')
    discussions.value = await res.json()
  } catch (e: unknown) {
    if (e instanceof Error) {
      error.value = e.message
    } else {
      error.value = 'Onbekende fout'
    }
  } finally {
    loading.value = false
  }
})

// ✅ Chronologisch sorteren (laatste activiteit bovenaan)
const sorted = computed(() =>
  [...discussions.value].sort(
    (a, b) =>
      new Date(b.lastActivityAt ?? '').getTime() -
      new Date(a.lastActivityAt ?? '').getTime()
  )
)

// ✅ Klik naar detailpagina
function openDetail(id: string) {
  router.push({ name: 'discussion-detail', params: { id } })
}

// ✅ Nieuw topic aanmaken (modal + form)
const showModal = ref(false)
const form = ref({ title: '', body: '' })

function openCreateModal() {
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  form.value = { title: '', body: '' }
}

async function createDiscussion() {
  try {
    const res = await fetch('http://localhost:8080/api/discussions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer testtoken123' // tijdelijk token
      },
      body: JSON.stringify(form.value)
    })

    if (!res.ok) throw new Error('Kon discussie niet aanmaken')
    const newDiscussion = await res.json()

    // ✅ Nieuwe discussie bovenaan toevoegen
    discussions.value.unshift(newDiscussion)
    closeModal()
  } catch (err) {
    console.error(err)
    alert('Er ging iets mis bij het plaatsen van je topic')
  }
}
</script>

<template>
  <main class="flex flex-col items-center bg-[--color-background] text-[--color-text-base] min-h-screen">
    <div class="flex flex-col items-center w-full mt-[75px] gap-8 px-6 max-w-5xl">
      <!-- Titel -->
      <div class="flex flex-col items-center gap-3 text-center">
        <h1 class="text-3xl font-bold">Forum Discussies</h1>
        <p class="text-[--color-text-muted] text-lg max-w-[680px]">
          <strong>Lees</strong> mee met de gesprekken,
          <strong>deel</strong> je mening en
          <strong>ontdek</strong> wat anderen denken over de verkiezingen.
        </p>
      </div>

      <!-- Lijst met discussies -->
      <div class="flex flex-col w-full gap-6 mt-4">
        <!-- Laden -->
        <div v-if="loading" class="text-lg text-center">Laden...</div>

        <!-- Foutmelding -->
        <div v-else-if="error" class="text-red-400 text-center">
          {{ error }}
        </div>

        <!-- Lijst met items -->
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
          <h2 class="text-xl font-semibold text-[--color-primary] mb-2">
            {{ d.title }}
          </h2>
          <p class="text-sm text-[--color-text-muted]">
            door <span class="font-medium">{{ d.author }}</span> •
            {{ d.reactionsCount }} reacties
          </p>
        </div>
      </div>

      <!-- Knop om nieuwe discussie te starten -->
      <div class="mt-8">
        <button
          @click="openCreateModal"
          class="px-6 py-3 rounded-lg font-semibold bg-[--color-primary] text-[--color-secondary] hover:opacity-90 transition"
        >
          Nieuw topic starten
        </button>
      </div>
    </div>

    <!-- Modal voor nieuw topic -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div
        class="bg-[--color-surface] rounded-2xl p-6 w-[500px] max-w-[90%] text-white shadow-xl border border-[rgba(255,255,255,0.1)]"
      >
        <h2 class="text-xl font-semibold mb-4 text-[--color-primary]">Nieuw topic</h2>

        <form @submit.prevent="createDiscussion" class="flex flex-col gap-4">
          <div>
            <label class="block text-sm mb-1">Titel</label>
            <input
              v-model="form.title"
              type="text"
              class="w-full p-2 rounded bg-gray-800 text-white border border-gray-600"
              required
            />
          </div>

          <div>
            <label class="block text-sm mb-1">Bericht</label>
            <textarea
              v-model="form.body"
              rows="5"
              class="w-full p-2 rounded bg-gray-800 text-white border border-gray-600"
              required
            ></textarea>
          </div>

          <div class="flex justify-end gap-3 mt-3">
            <button type="button" @click="closeModal" class="btn btn-secondary">Annuleren</button>
            <button type="submit" class="btn btn-primary">Plaatsen</button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>
