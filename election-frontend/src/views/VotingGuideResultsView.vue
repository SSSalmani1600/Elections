<script setup lang="ts">
import type { VotingGuideResult, VotingGuideResultResponse } from '@/types/api.ts'
import { onMounted, ref } from 'vue'
import ProgressBar from '@/components/ProgressBar.vue'
import router from '@/router'
import { Spinner } from '@/components/ui/spinner'
import { useAuth } from '@/store/authStore.ts'
import { getResults } from '@/services/VotingGuideResultsService.ts'

const results = ref<VotingGuideResultResponse>({
  votingGuideResults: [],
})
const resultsTop3 = ref<VotingGuideResult[]>()
const restOfResults = ref<VotingGuideResult[]>()
const isLoadingResults = ref<boolean>(false)
const resultsIsEmpty = ref<boolean>(false)
const { user } = useAuth()

onMounted(async () => {
  isLoadingResults.value = true

  try {
    if (user.value) {
      results.value = await getResults()
      if (results.value.votingGuideResults !== undefined) localStorage.removeItem("voting_guide_answers")
    } else {
      const resultsRaw = localStorage.getItem('voting_guide_results')
      results.value = resultsRaw ? JSON.parse(resultsRaw) : null
    }

    // Send user back to voting guide when results are non-existing
    if (results.value == null || results.value?.votingGuideResults.length === 0) {
      resultsIsEmpty.value = true
      setTimeout(() => router.replace({ name: 'voting-guide' }), 3000)
      return
    }

    resultsTop3.value = results.value?.votingGuideResults.slice(0, 3)
    restOfResults.value = results.value.votingGuideResults.slice(3)
    isLoadingResults.value = false
  } catch(err: any) {
    console.error(err.message)
  }
})
</script>

<template>
  <div class="flex flex-col items-center w-full my-10 gap-8">
    <div class="flex flex-col gap-2 items-center mx-4 text-center">
      <h1 class="text-4xl font-bold">Jouw Resultaten</h1>
      <p>Op basis van jouw antwoorden passen deze partijen het beste bij jou</p>
    </div>

    <div
      v-if="resultsIsEmpty"
      class="bg-background p-10 rounded-[10px] absolute transform translate-[-50%] top-1/2 left-1/2"
    >
      <div class="flex items-center w-full gap-2">
        <span class="text-red-400 font-bold text-xl flex items-center gap-2"
          >ERROR <Spinner></Spinner
        ></span>
        <div class="relative w-full flex">
          <span class="w-full h-2 rounded-lg bg-white"></span>
          <span class="progress w-full h-2 rounded-lg bg-primary absolute transition"></span>
        </div>
      </div>
      <p class="text-lg">
        Er zijn geen bekende resultaten, u wordt terug gestuurd naar de stemwijzer
      </p>
    </div>
    <div v-else class="flex flex-col gap-8 w-full items-center">
      <div
        class="flex flex-col gap-5 justify-between w-[90%] md:w-[70%] lg:flex-row lg:h-[290px] lg:gap-10 2xl:w-[55%]"
      >
        <div
          v-for="(party, index) in resultsTop3"
          :key="party.partyId"
          class="flex flex-col w-full h-full bg-background rounded-lg p-3.5 justify-between shadow-lg gap-1 lg:gap-0"
        >
          <div class="flex flex-col gap-2">
            <div class="flex items-center gap-2">
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

            <span class="font-bold text-xl mb-4 lg:mb-0">{{ party.partyName }}</span>
          </div>
          <div class="flex flex-col gap-1">
            <span class="text-xl font-bold">{{ Math.ceil(Number(party.percentage)) }}%</span>
            <ProgressBar :percentage="Math.ceil(Number(party.percentage))"></ProgressBar>
          </div>
        </div>
      </div>
      <div class="flex flex-col gap-4 h-[400px] w-[85%] overflow-y-scroll md:w-[60%] lg:w-1/2">
        <div
          v-for="party in restOfResults"
          :key="party.partyId"
          class="flex flex-col w-full justify-between shadow-lg bg-background p-3 rounded-lg items-center gap-3 lg:gap-0 lg:flex-row"
        >
          <div class="w-full flex flex-col">
            <span class="text-xl font-bold">{{ party.partyName }}</span>
            <span class="text-sm truncate lg:text-md">Overeenkomst met jouw antwoorden</span>
          </div>
          <div class="w-full flex flex-col gap-2.5 lg:w-1/3 lg:text-right">
            <span class="font-bold text-xl">{{ Math.ceil(Number(party.percentage)) }}%</span>
            <ProgressBar
              class="!h-1.5"
              :percentage="Math.ceil(Number(party.percentage))"
            ></ProgressBar>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.progress {
  animation: shrink 3s forwards;
}

@keyframes shrink {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}
</style>
