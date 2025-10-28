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
    <section class="max-w-4xl mx-auto px-6 pt-24 pb-16">
      <!-- Terug -->
      <button
        @click="backToList"
        class="mb-6 px-4 py-2 rounded-xl bg-[--color-primary] text-white hover:opacity-90 transition"
      >
        ← Terug naar forum
      </button>

      <!-- Loading / Error -->
      <div v-if="loading" class="text-lg">Laden…</div>
      <div v-else-if="error" class="text-red-500 text-lg">{{ error }}</div>

      <!-- Detail -->
      <div v-else-if="discussion" class="bg-white/5 rounded-2xl p-6 border border-white/10">
        <h1 class="text-3xl font-bold mb-2">{{ discussion.title }}</h1>
        <p class="text-sm text-[--color-text-muted] mb-6">
          Door {{ discussion.author }} ·
          Laatste activiteit: {{ new Date(discussion.lastActivityAt).toLocaleString() }} ·
          Reacties: {{ discussion.reactionsCount }}
        </p>

        <article class="prose prose-invert max-w-none">
          <p class="whitespace-pre-line">{{ discussion.body }}</p>
        </article>

        <!-- Placeholder voor reacties -->
        <div class="mt-10">
          <h2 class="text-2xl font-semibold mb-3">Reacties</h2>
          <p class="text-[--color-text-muted]">
            (Nog geen afzonderlijke reacties-objecten in backend — teller is
            {{ discussion.reactionsCount }}.)
          </p>
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
