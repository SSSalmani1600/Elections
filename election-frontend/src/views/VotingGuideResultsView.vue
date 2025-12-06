<script setup lang="ts">
import type { VotingGuideResultResponse } from '@/types/api.ts'
import { onMounted, ref } from 'vue'
import ProgressBar from '@/components/ProgressBar.vue'

const results = ref<VotingGuideResultResponse>({
  votingGuideResults: [],
})
const isLoadingResults = ref<boolean>(false)

onMounted(async () => {
  isLoadingResults.value = true

  results.value = JSON.parse(localStorage.getItem('voting_guide_results') || '[]')
  isLoadingResults.value = false

  console.log(results.value)
})
</script>

<template>
  <div class="flex flex-col items-center w-full">
    <div>
      <h1>Jouw Resultaten</h1>
      <p>Op basis van jouw antwoorden passen deze partijen het beste bij jou</p>
    </div>

    <div class="flex flex-col gap-4 w-1/2">
      <div v-for="party in results.votingGuideResults" :key="party.partyId" class="flex w-full justify-between shadow-lg bg-background p-4 rounded-lg items-center">
        <div class="w-full flex flex-col">
          <span class="text-xl">{{ party.partyName }}</span>
          <span>Overeenkomst met jouw mening</span>
        </div>
        <div class="text-right w-1/3">
          <span class="font-bold text-xl">{{ Math.ceil(Number(party.percentage)) }}%</span>
          <ProgressBar class="!h-1.5" :percentage="Math.ceil(Number(party.percentage))"></ProgressBar>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
