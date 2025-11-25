<script setup lang="ts">
import { ref } from "vue"

const { poll } = defineProps<{
  poll: {
    id: string
    question: string
  }
}>()

const loading = ref(false)
const hasVoted = ref(false)

const percentages = ref({ eens: 0, oneens: 0 })

function vote(choice: "eens" | "oneens") {
  loading.value = true

  setTimeout(() => {
    hasVoted.value = true
    percentages.value =
      choice === "eens"
        ? { eens: 72, oneens: 28 }
        : { eens: 40, oneens: 60 }

    loading.value = false
  }, 900)
}
</script>

<template>
  <div
    class="poll-card max-w-3xl mx-auto p-8 rounded-2xl shadow-xl"
  >
    <p class="text-2xl font-semibold mb-6 leading-snug">
      {{ poll.question }}
    </p>

    <div
      v-if="!hasVoted"
      class="flex gap-4 max-sm:flex-col"
    >
      <button
        class="btn btn-primary flex-1 py-3 text-lg"
        :disabled="loading"
        @click="vote('eens')"
      >
        <span v-if="!loading">Eens</span>
        <span v-else class="animate-pulse">Even wachten...</span>
      </button>

      <button
        class="btn btn-secondary flex-1 py-3 text-lg"
        :disabled="loading"
        @click="vote('oneens')"
      >
        <span v-if="!loading">Oneens</span>
        <span v-else class="animate-pulse">Even wachten...</span>
      </button>
    </div>

    <div
      v-else
      class="mt-6 bg-white/10 rounded-xl px-6 py-4 animate-fadeIn flex justify-between text-lg font-semibold"
    >
      <span class="text-green-400">Eens: {{ percentages.eens }}%</span>
      <span class="text-red-400">Oneens: {{ percentages.oneens }}%</span>
    </div>
  </div>
</template>

<style scoped>
.poll-card {
  background: #0f1730;        /* past perfect bij jouw hero */
  border: 1px solid #ffffff20;
  color: white;
  box-shadow: 0 6px 20px rgba(0,0,0,0.35);
  transition: 0.3s ease;
}

.poll-card:hover {
  box-shadow: 0 10px 28px rgba(0,0,0,0.45);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to   { opacity: 1; transform: translateY(0); }
}
.animate-fadeIn {
  animation: fadeIn 0.35s ease-out;
}
</style>
