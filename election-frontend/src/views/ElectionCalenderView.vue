<template>
  <section class="calendar">
    <h1>Upcoming Elections</h1>

    <div v-if="loading">Loading elections...</div>
    <div v-else-if="error">{{ error }}</div>

    <table v-else>
      <thead>
      <tr>
        <th>Type</th>
        <th>Date</th>
        <th>Status</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="election in elections" :key="election.id">
        <td>{{ election.type }}</td>
        <td>{{ formatDate(election.date) }}</td>
        <td>
            <span
              :class="{
                confirmed: election.status === 'confirmed',
                projected: election.status === 'projected'
              }"
            >
              {{ election.status }}
            </span>
        </td>
      </tr>
      </tbody>
    </table>
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
  } catch  {
    error.value = 'Failed to load elections'
  } finally {
    loading.value = false
  }
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric'
  })
}

onMounted(loadElections)
</script>

<style scoped>
.calendar {
  margin: 2rem auto;
  max-width: 800px;
  font-family: sans-serif;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

th, td {
  border: 1px solid #ccc;
  padding: 0.5rem;
  text-align: left;
}

.confirmed {
  color: green;
  font-weight: bold;
}

.projected {
  color: orange;
  font-weight: bold;
}
</style>
