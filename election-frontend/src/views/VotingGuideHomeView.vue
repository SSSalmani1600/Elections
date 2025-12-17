<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getAllCategories } from '@/services/StatementService.ts'
import { useToast } from 'primevue'
import type { VotingGuideParty } from '@/types/api.ts'
import { getAllVotingGuideParties } from '@/services/VotingGuidePartiesService.ts'

const toast = useToast()
const array = ref<{ title: string; text: string }[]>([
  {
    title: 'Bekijk alle stellingen',
    text: 'Je kan zelf bepalen welke vraag jij wilt beantwoorden.',
  },
  {
    title: 'Beantwoord de stellingen met jouw mening',
    text: 'Beantwoord elke stelling met EENS, NEUTRAAL of ONEENS, door op 1 van de knoppen te klikken.',
  },
  {
    title: 'Bekijk je resultaat',
    text: 'Na afloop krijg je jouw persoonlijke top 3 te zien, plus een overzicht van de overige partijen.',
  },
])
const categories = ref<string[]>()
const parties = ref<VotingGuideParty[]>()

onMounted(async () => {
  try {
    categories.value = await getAllCategories()
  } catch (err: any) {
    console.error(err.message)
    toast.add({
      severity: 'error',
      summary: 'Fout bij ophalen',
      detail: 'Er ging iets mis met het ophalen van de categorieen',
      life: 3000,
    })
  }

  try {
    parties.value = await getAllVotingGuideParties()
  } catch (err: any) {
    console.error(err.message)
    toast.add({
      severity: 'error',
      summary: 'Fout bij ophalen',
      detail: 'Er ging iets mis met het ophalen van de partijen',
      life: 3000,
    })
  }
})
</script>

<template>
  <div class="flex flex-col items-center m-10 gap-10">
    <div class="flex flex-col items-center gap-2">
      <h1 class="font-bold text-3xl">Stemwijzer 2025</h1>
      <p class="w-[80%] text-center text-text-muted">
        Ontdek welke partij of kandidaat het beste aansluit bij jouw mening. Beantwoord stellingen,
        kies wat jij belangrijk vindt en vergelijk jouw resultaat.
      </p>
    </div>
    <router-link to="/stemwijzer/invullen" class="btn btn-primary">Doe de stemwijzer</router-link>

    <div class="flex flex-col w-[80%] gap-3 items-center">
      <h4 class="font-bold text-2xl">Hoe werkt het?</h4>
      <div class="grid grid-cols-3 gap-10">
        <div
          class="col-span-1 flex flex-col gap-2 p-4 bg-background h-full rounded-lg shadow-lg text-center"
          v-for="(item, index) in array"
          :key="index"
        >
          <span class="font-bold text-lg"
            ><span class="text-primary">{{ index + 1 }}.</span> {{ item.title }}</span
          >
          <p class="text-text-muted">{{ item.text }}</p>
        </div>
      </div>
    </div>

    <div class="flex flex-col items-center gap-3 w-[70%]">
      <h4 class="font-bold text-2xl">De categorieen - {{ categories?.length }}</h4>
      <div class="flex flex-wrap gap-2 justify-center">
        <span
          v-for="category in categories"
          :key="category"
          class="bg-background p-2 px-4 shadow-lg rounded-lg"
          >{{ category }}</span
        >
      </div>
    </div>

    <div class="w-[80%] flex flex-col items-center gap-3">
      <h4 class="font-bold text-2xl">Deelnemende partijen - {{ parties?.length }}</h4>
      <div class="flex flex-wrap gap-2 justify-center">
        <span v-for="party in parties" :key="party.id" class="bg-background p-2 px-4 rounded-lg">{{
          party.name
        }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
