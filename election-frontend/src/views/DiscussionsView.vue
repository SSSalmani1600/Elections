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

    discussions.value.unshift(newDiscussion)
    closeModal()
  } catch (err) {
    console.error(err)
    alert('Er ging iets mis bij het plaatsen van je topic.')
  }
}
</script>

<template>
  <main
    class="flex flex-col items-center bg-[--color-background] text-[--color-text-base] min-h-screen"
  >
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

      <!-- 🔹 Knop om nieuw topic te starten -->
      <!-- Knop om nieuw topic te starten -->
      <button
        @click="showModal = true"
        class="px-7 py-3 rounded-xl font-semibold
         bg-gradient-to-r from-[#d82f4c] to-[#ef3054]
         text-white
         shadow-[0_2px_10px_rgba(239,48,84,0.25)]
         hover:shadow-[0_3px_15px_rgba(239,48,84,0.35)]
         hover:scale-[1.02]
         transition-all duration-200 ease-out
         border border-[rgba(255,255,255,0.06)]
         mt-2"
      >
        <span class="tracking-wide">➕ Nieuw topic starten</span>
      </button>



      <!-- 🔹 Lijst met discussies -->
      <div class="flex flex-col w-full gap-6 mt-6">
        <div v-if="loading" class="text-lg text-center">Laden...</div>
        <div v-else-if="error" class="text-red-400 text-center">
          {{ error }}
        </div>

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
    </div>

    <!-- 🌙 Modal: Nieuw topic -->
    <div
      v-if="showModal"
      class="fixed inset-0 bg-black/70 flex items-center justify-center z-50 backdrop-blur-sm"
    >
      <div
        class="bg-gradient-to-b from-[#1b2240] to-[#0e142b]
               p-8 rounded-3xl w-[500px] max-w-[90%] text-white shadow-[0_0_30px_rgba(79,70,229,0.4)]
               border border-[rgba(255,255,255,0.08)] animate-fadeIn"
      >
        <h2 class="text-2xl font-bold mb-4 text-[--color-primary] text-center">
          Nieuw topic starten
        </h2>

        <form @submit.prevent="createDiscussion" class="flex flex-col gap-4">
          <div>
            <label class="text-sm mb-1 block text-[--color-text-muted]">Titel</label>
            <input
              v-model="form.title"
              type="text"
              required
              class="w-full bg-[#121830] border border-[rgba(255,255,255,0.1)] rounded-xl p-3 text-white focus:outline-none focus:ring-2 focus:ring-[--color-primary]"
              placeholder="Bijv. Wat vind jij van de verkiezingen?"
            />
          </div>

          <div>
            <label class="text-sm mb-1 block text-[--color-text-muted]">Bericht</label>
            <textarea
              v-model="form.body"
              rows="5"
              required
              class="w-full bg-[#121830] border border-[rgba(255,255,255,0.1)] rounded-xl p-3 text-white resize-none focus:outline-none focus:ring-2 focus:ring-[--color-primary]"
              placeholder="Schrijf hier je mening of vraag..."
            ></textarea>
          </div>

          <div class="flex justify-end gap-3 mt-6">
            <button
              type="button"
              @click="closeModal"
              class="px-5 py-2 rounded-lg bg-[rgba(255,255,255,0.1)] text-white hover:bg-[rgba(255,255,255,0.2)] transition"
            >
              Annuleren
            </button>
            <button
              type="submit"
              class="px-5 py-2 rounded-lg bg-[--color-primary] text-white hover:opacity-90 shadow-md"
            >
              Plaatsen
            </button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
.animate-fadeIn {
  animation: fadeIn 0.25s ease-out;
}
</style>
