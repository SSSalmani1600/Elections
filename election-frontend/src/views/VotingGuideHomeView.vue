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
  <div>
    <h1>Stemwijzer 2025</h1>
    <p>
      Ontdek welke partij of kandidaat het beste aansluit bij jouw mening. Beantwoord stellingen,
      kies wat jij belangrijk vindt en vergelijk jouw resultaat.
    </p>
  </div>
  <router-link to="/stemwijzer/invullen">Doe de stemwijzer</router-link>

  <div>
    <h4>Hoe werkt het?</h4>
    <div>
      <div v-for="(item, index) in array" :key="index">
        <span>{{ index + 1 }}. {{ item.title }}</span>
        <p>{{ item.text }}</p>
      </div>
    </div>
  </div>

  <div>
    <div>
      <span v-for="category in categories" :key="category">{{ category }}</span>
    </div>
  </div>

  <div>
    <h4>Deelnemende partijen</h4>
    <div>
      <span v-for="party in parties" :key="party.id">{{ party.name }}</span>
    </div>
  </div>
</template>

<style scoped></style>
