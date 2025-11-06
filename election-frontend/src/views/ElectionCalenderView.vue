<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getUpcomingElections, type Election } from '@/services/ElectionService.ts'

const elections = ref<Election[]>([])
const loading = ref(true)
const error = ref('')

const pageSize = ref(3)
const currentPage = ref(1)

async function loadElections() {
  try {
    elections.value = await getUpcomingElections()
  } catch {
    error.value = 'Kon verkiezingen niet laden'
  } finally {
    loading.value = false
  }
}


function formatDay(dateString: string) {
  const d = new Date(dateString)
  return isNaN(d.getTime()) ? '?' : d.getDate().toString().padStart(2, '0')
}

function formatMonth(dateString: string) {
  const maanden = ['Jan', 'Feb', 'Mrt', 'Apr', 'Mei', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dec']
  const d = new Date(dateString)
  return isNaN(d.getTime()) ? '' : maanden[d.getMonth()]
}

function formatFullDate(dateString: string) {
  const d = new Date(dateString)
  return isNaN(d.getTime())
    ? dateString
    : d.toLocaleDateString('nl-NL', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    })
}

function translateType(type: string): string {
  switch (type) {
    case 'Municipal elections':
      return 'Gemeenteraadsverkiezingen'
    case 'Provincial elections':
      return 'Provinciale Statenverkiezingen'
    case 'Parliamentary elections':
      return 'Tweede Kamerverkiezingen'
    default:
      return type
  }
}

function translateStatus(status: string): string {
  if (status === 'confirmed') return 'Bevestigd'
  if (status === 'projected') return 'Verwacht'
  return status
}


const totalPages = computed(() => Math.ceil(elections.value.length / pageSize.value))
const visibleElections = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = currentPage.value * pageSize.value
  return elections.value.slice(start, end)
})

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

onMounted(loadElections)
</script>

<template>
  <section
    class="flex flex-col items-center min-h-screen py-16 px-4 text-white bg-[var(--color-surface)]"
  >
    <h1 class="text-4xl font-bold mb-10 text-center max-md:text-3xl">
      Verkiezingskalender
    </h1>


    <div v-if="loading" class="text-[var(--color-text-muted)] text-lg mt-4">
      Verkiezingen worden geladen...
    </div>
    <div v-else-if="error" class="text-red-400 text-lg mt-4">{{ error }}</div>


    <div v-else class="flex flex-col gap-6 w-full max-w-3xl">
      <div
        v-for="election in visibleElections"
        :key="election.id"
        class="flex items-center bg-[#0B132B] rounded-2xl p-6 shadow-md hover:bg-[#101b3d] transition duration-200 border border-[#1C2541]"
      >

        <div class="text-center mr-6 pr-6 border-r border-[rgba(255,255,255,0.15)] min-w-[80px]">
          <div class="text-3xl font-bold text-[var(--color-secondary)]">
            {{ formatDay(election.date) }}
          </div>
          <div class="text-sm text-[var(--color-text-muted)] uppercase tracking-wide">
            {{ formatMonth(election.date) }}
          </div>
        </div>


        <div class="flex flex-col flex-1">
          <h2 class="text-xl font-semibold mb-1 text-[var(--color-secondary)]">
            {{ translateType(election.type) }}
          </h2>
          <p class="text-[var(--color-text-muted)] mb-1">
            {{ formatFullDate(election.date) }} <br />
            📍 Nederland
          </p>
          <p
            class="font-semibold capitalize"
            :class="{
              'text-green-400': election.status === 'confirmed',
              'text-yellow-400': election.status === 'projected'
            }"
          >
            {{ translateStatus(election.status) }}
          </p>
        </div>
      </div>


      <div
        v-if="!elections.length && !loading && !error"
        class="text-center text-[var(--color-text-muted)] mt-8"
      >
        Geen aankomende verkiezingen gevonden.
      </div>


      <div
        v-if="totalPages > 1"
        class="flex items-center justify-center gap-4 mt-10"
      >
        <button
          class="pagination-btn"
          @click="goToPage(currentPage - 1)"
          :disabled="currentPage === 1"
        >
          Vorige
        </button>

        <div class="flex gap-2">
          <button
            v-for="page in totalPages"
            :key="page"
            class="page-option"
            :class="{
              'active-page': page === currentPage
            }"
            @click="goToPage(page)"
          >
            {{ page }}
          </button>
        </div>

        <button
          class="pagination-btn"
          @click="goToPage(currentPage + 1)"
          :disabled="currentPage === totalPages"
        >
          Volgende
        </button>
      </div>
    </div>
  </section>
</template>

