<script setup lang="ts">
import type { VotingGuideResult, VotingGuideResultResponse } from '@/types/api.ts'
import { onMounted, ref } from 'vue'
import ProgressBar from '@/components/ProgressBar.vue'

const results = ref<VotingGuideResultResponse>({
  votingGuideResults: [],
})
const resultsTop3 = ref<VotingGuideResult[]>()
const restOfResults = ref<VotingGuideResult[]>()
const isLoadingResults = ref<boolean>(false)

onMounted(async () => {
  isLoadingResults.value = true

  results.value = JSON.parse(localStorage.getItem('voting_guide_results') || '[]')

  resultsTop3.value = results.value.votingGuideResults.slice(0, 3)
  restOfResults.value = results.value.votingGuideResults.slice(3)
  isLoadingResults.value = false

  console.log(results.value)
})
</script>

<template>
  <div class="flex flex-col items-center w-full my-10 gap-8 ">
    <div class="flex flex-col gap-2 items-center">
      <h1 class="text-4xl font-bold">Jouw Resultaten</h1>
      <p>Op basis van jouw antwoorden passen deze partijen het beste bij jou</p>
    </div>

    <div class="flex gap-10 justify-between h-[290px] w-[55%]">
      <div
        v-for="(party, index) in resultsTop3"
        :key="party.partyId"
        class="flex flex-col w-full h-full bg-background rounded-lg p-3.5 justify-between shadow-lg"
      >
        <div class="flex gap-2 items-center">
          <i
            class="pi pi-crown"
            :class="{
              'text-yellow-400': index === 0,
              'text-gray-400': index === 1,
              'text-amber-600': index === 2,
            }"
          ></i>
          <span class="font-bold text-xl">{{ index + 1 }}e plaats</span>
        </div>
        <div class="flex flex-col gap-1 text-xl font-bold">
          <span>{{ party.partyName }}</span>
          <span>{{ Math.ceil(Number(party.percentage)) }}%</span>
          <ProgressBar :percentage="Math.ceil(Number(party.percentage))"></ProgressBar>
        </div>
      </div>
    </div>
    <div class="flex flex-col gap-4 w-1/2 h-[400px] overflow-scroll">
      <div
        v-for="party in restOfResults"
        :key="party.partyId"
        class="flex w-full justify-between shadow-lg bg-background p-3 rounded-lg items-center"
      >
        <div class="w-full flex flex-col">
          <span class="text-xl">{{ party.partyName }}</span>
          <span>Overeenkomst met jouw antwoorden</span>
        </div>
        <div class="text-right w-1/3 flex flex-col gap-2.5">
          <span class="font-bold text-xl">{{ Math.ceil(Number(party.percentage)) }}%</span>
          <ProgressBar
            class="!h-1.5"
            :percentage="Math.ceil(Number(party.percentage))"
          ></ProgressBar>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
