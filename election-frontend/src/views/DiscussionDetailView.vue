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

