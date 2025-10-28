<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

// Type voor een discussie-item
type Discussion = {
  id: number
  title: string
  author: string
  replies: number
  lastActivityAt?: string
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
    if (e instanceof Error) {
      error.value = e.message
    } else {
      error.value = 'Onbekende fout'
    }
  }
  finally {
    loading.value = false
  }
})

// Chronologisch sorteren (laatste activiteit bovenaan)
const sorted = computed(() =>
  [...discussions.value].sort(
    (a, b) =>
      new Date(b.lastActivityAt ?? '').getTime() -
      new Date(a.lastActivityAt ?? '').getTime()
  )
)

// Klik naar detailpagina
function openDetail(id: number) {
  router.push({ name: 'discussion-detail', params: { id } })
}
</script>

<template>
  <main
    class="flex flex-col items-center bg-[--color-background] text-[--color-text-base] min-h-screen"
  >
    <div
      class="flex flex-col items-center w-full mt-[75px] gap-8 px-6 max-w-5xl"
    >
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
            {{ d.replies }} reacties
          </p>
        </div>
      </div>

      <!-- Call To Action -->
      <div class="mt-8">
        <button
          class="px-6 py-3 rounded-lg font-semibold bg-[--color-primary] text-[--color-secondary] hover:opacity-90 transition"
        >
          Nieuwe discussie starten
        </button>
      </div>
    </div>
  </main>
</template>
