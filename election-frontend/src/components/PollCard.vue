<script setup lang="ts">
import { ref, onMounted } from "vue"
import { votePoll, getMyVote } from "@/services/PollService"

const { poll } = defineProps<{
  poll: {
    id: string
    question: string
  }
}>()

const loading = ref(false)
const hasVoted = ref(false)
const isCheckingVote = ref(true)
const errorMessage = ref("")
const percentages = ref({ eens: 0, oneens: 0 })

function setPercentages(result: { eens: number; oneens: number; total: number }) {
  percentages.value = {
    eens: Math.round((result.eens / result.total) * 100),
    oneens: Math.round((result.oneens / result.total) * 100)
  }
}

async function loadUserVote() {
  try {
    const result = await getMyVote(poll.id)

    if (result) {
      setPercentages(result)
      hasVoted.value = true
    }
  } catch (e) {
    console.error("Kon user vote status niet ophalen", e)
  } finally {
    isCheckingVote.value = false
  }
}

async function vote(choice: "eens" | "oneens") {
  if (hasVoted.value) return // extra safety

  loading.value = true
  errorMessage.value = ""

  try {
    const result = await votePoll(poll.id, choice)
    setPercentages(result)
    hasVoted.value = true

  } catch (err: any) {
    if (err.message?.includes("gestemd")) {
      await loadUserVote()
      hasVoted.value = true
    } else {
      errorMessage.value = "Stemmen mislukt"
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadUserVote)
</script>

<template>
  <div class="poll-card max-w-3xl mx-auto p-8 rounded-2xl shadow-xl">

    <p class="text-2xl font-semibold mb-6 leading-snug">
      {{ poll.question }}
    </p>

    <p v-if="errorMessage" class="text-red-400 mb-4 text-sm">
      {{ errorMessage }}
    </p>

    <div
      v-if="!hasVoted && !isCheckingVote"
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
      v-else-if="hasVoted"
      class="mt-6 bg-white/10 rounded-xl px-6 py-4 animate-fadeIn relative overflow-hidden"
    >
      <div class="result-progress">
        <div
          class="result-green"
          :style="{ width: percentages.eens + '%' }"
        ></div>

        <div
          class="result-red"
          :style="{ width: percentages.oneens + '%' }"
        ></div>
      </div>

      <div class="flex justify-between text-lg font-semibold relative z-10">
        <span class="text-green-400">Eens: {{ percentages.eens }}%</span>
        <span class="text-red-400">Oneens: {{ percentages.oneens }}%</span>
      </div>
    </div>

    <div v-else class="text-gray-400 text-center">
      Resultaten laden…
    </div>

  </div>
</template>

<style scoped>
.poll-card {
  background: #0f1730;
  border: 1px solid #ffffff20;
  color: white;
  box-shadow: 0 6px 20px rgba(0,0,0,0.35);
  transition: 0.3s ease;
}

.poll-card:hover {
  box-shadow: 0 10px 28px rgba(0,0,0,0.45);
}

.result-progress {
  position: absolute;
  inset: 0;
  display: flex;
  pointer-events: none;
}

.result-green {
  height: 100%;
  background: rgba(34, 197, 94, 0.25);
  transition: width 0.5s ease;
}

.result-red {
  height: 100%;
  background: rgba(239, 68, 68, 0.25);
  transition: width 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to   { opacity: 1; transform: translateY(0); }
}

.animate-fadeIn {
  animation: fadeIn 0.35s ease-out;
}
</style>
