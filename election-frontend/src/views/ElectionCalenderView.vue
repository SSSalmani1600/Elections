<template>
  <section class="calendar">
    <h1>Verkiezingskalender</h1>

    <div v-if="loading" class="loading">Verkiezingen worden geladen...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else class="calendar-list">
      <div
        v-for="election in elections"
        :key="election.id"
        class="calendar-item"
      >
        <div class="date-box">
          <div class="day">{{ formatDay(election.date) }}</div>
          <div class="month">{{ formatMonth(election.date) }}</div>
        </div>

        <div class="details">
          <h2 class="type">{{ translateType(election.type) }}</h2>
          <p class="date-line">
             {{ formatFullDate(election.date) }}
            <br />
            📍 Nederland
          </p>
          <p
            class="status"
            :class="{
              confirmed: election.status === 'confirmed',
              projected: election.status === 'projected'
            }"
          >
            {{ translateStatus(election.status) }}
          </p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUpcomingElections, type Election } from '@/services/ElectionService.ts'

const elections = ref<Election[]>([])
const loading = ref(true)
const error = ref('')

async function loadElections() {
  try {
    elections.value = await getUpcomingElections()
  } catch {
    error.value = 'Kon verkiezingen niet laden'
  } finally {
    loading.value = false
  }
}

// ✅ Datum helpers
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
      year: 'numeric',
    })
}

// ✅ Vertaalhelpers
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

onMounted(loadElections)
</script>

<style scoped>
.calendar {
  max-width: 800px;
  margin: 2rem auto;
  font-family: system-ui, sans-serif;
  color: #fff;
}

h1 {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  text-align: center;
}

.calendar-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.calendar-item {
  display: flex;
  background: #1d2233;
  border-radius: 12px;
  padding: 1rem 1.5rem;
  align-items: center;
  transition: background 0.2s;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.25);
}

.calendar-item:hover {
  background: #242b40;
}

.date-box {
  text-align: center;
  margin-right: 1.5rem;
  border-right: 2px solid #444;
  padding-right: 1.5rem;
  min-width: 70px;
}

.day {
  font-size: 2rem;
  font-weight: bold;
  color: #fff;
}

.month {
  font-size: 1rem;
  color: #aaa;
}

.details {
  flex: 1;
}

.type {
  font-size: 1.2rem;
  margin-bottom: 0.4rem;
  font-weight: bold;
}

.date-line {
  color: #ccc;
  margin-bottom: 0.4rem;
}

.status {
  font-weight: bold;
  text-transform: capitalize;
}

.confirmed {
  color: #00d26a;
}

.projected {
  color: #ffb347;
}

.loading,
.error {
  text-align: center;
  margin-top: 2rem;
}
</style>
