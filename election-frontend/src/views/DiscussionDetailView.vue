<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

type DiscussionDetail = {
  id: string
  title: string
  author: string
  body: string
  createdAt: string
  lastActivityAt: string
  reactionsCount: number
}

const loading = ref(true)
const error = ref<string | null>(null)
const discussion = ref<DiscussionDetail | null>(null)

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
</script>

<template>
  <main class="min-h-screen bg-[--color-background] text-[--color-text-base]">
    <section class="max-w-3xl mx-auto px-6 py-24">
      <!-- 🔙 Terugknop -->
      <button
        @click="backToList"
        class="flex items-center gap-2 mb-10 text-[--color-primary] font-medium hover:underline"
      >
        <span>←</span> Terug naar forum
      </button>

      <!-- 🌀 Status -->
      <div v-if="loading" class="text-lg text-center">Even geduld…</div>
      <div v-else-if="error" class="text-red-400 text-center text-lg">{{ error }}</div>

      <!-- 💬 Detailkaart -->
      <div
        v-else-if="discussion"
        class="bg-[color:rgba(255,255,255,0.05)] rounded-2xl shadow-lg border border-[color:rgba(255,255,255,0.08)] p-10 transition hover:shadow-xl"
      >
        <!-- Titel + meta -->
        <h1 class="text-4xl font-bold mb-3 text-white tracking-tight">
          {{ discussion.title }}
        </h1>

        <div class="flex flex-wrap items-center gap-3 text-sm text-[--color-text-muted] mb-8">
          <span class="font-medium text-[--color-primary]">{{ discussion.author }}</span>
          <span>•</span>
          <span>Laatst actief: {{ new Date(discussion.lastActivityAt).toLocaleString() }}</span>
          <span>•</span>
          <span>{{ discussion.reactionsCount }} reacties</span>
        </div>

        <!-- Body -->
        <article
          class="prose prose-invert max-w-none leading-relaxed text-[--color-text-base] bg-[rgba(255,255,255,0.02)] p-6 rounded-xl border border-[rgba(255,255,255,0.06)]"
        >
          <p class="whitespace-pre-line text-lg">{{ discussion.body }}</p>
        </article>

        <!-- Reacties sectie -->
        <div class="mt-12">
          <h2 class="text-2xl font-semibold mb-4 text-white">Reacties</h2>

          <div
            v-if="discussion.reactionsCount === 0"
            class="text-[--color-text-muted] italic"
          >
            Nog geen reacties — wees de eerste om iets te zeggen!
          </div>

          <div
            v-else
            class="space-y-4 text-[--color-text-muted] bg-[rgba(255,255,255,0.02)] p-6 rounded-xl border border-[rgba(255,255,255,0.05)]"
          >
            <p class="text-sm">
              (Er zijn {{ discussion.reactionsCount }} reacties, maar ze worden nog niet opgehaald.)
            </p>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>
<style scoped>
.prose :where(p):not(:where(.not-prose *)) {
  line-height: 1.7;
}
</style>
