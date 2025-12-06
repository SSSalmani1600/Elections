<script setup lang="ts">

import type { VotingGuideResultResponse } from '@/types/api.ts'
import { onMounted, ref } from 'vue'

const results = ref<VotingGuideResultResponse>()
const isLoadingResults = ref<boolean>(false)

onMounted(async () => {
  isLoadingResults.value = true
  const jwtToken = localStorage.getItem('JWT') || null

  if (!jwtToken) {
      results.value = JSON.parse(localStorage.getItem('voting_guide_results') || '[]')
      isLoadingResults.value = false
  }

  console.log(results.value)
})

</script>

<template>
  Resultaten pagina
</template>

<style scoped></style>
