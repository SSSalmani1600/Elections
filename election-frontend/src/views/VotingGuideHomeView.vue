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
const loadingCategories = ref<boolean>(false)
const loadingParties = ref<boolean>(false)

const categoriesHasError = ref<boolean>(false)
const partiesHasError = ref<boolean>(false)

onMounted(async () => {
  loadingCategories.value = true
  loadingParties.value = true
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
    categoriesHasError.value = true
  } finally {
    loadingCategories.value = false
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
    partiesHasError.value = true
  } finally {
    loadingParties.value = false
  }
})
</script>

<template>
  <div class="flex flex-col items-center m-4 md:m-10 gap-10">
    <div class="flex flex-col items-center gap-2">
      <h1 class="font-bold text-3xl">Stemwijzer 2025</h1>
      <p class="w-[80%] text-center text-text-muted text-sm lg:text-md">
        Ontdek welke partij of kandidaat het beste aansluit bij jouw mening. Beantwoord stellingen,
        kies wat jij belangrijk vindt en vergelijk jouw resultaat.
      </p>
    </div>
    <router-link to="/stemwijzer/invullen" class="btn btn-primary">Doe de stemwijzer</router-link>

    <div class="flex flex-col gap-3 items-center lg:w-[80%]">
      <h4 class="font-bold text-2xl">Hoe werkt het?</h4>
      <div class="flex flex-col gap-6 lg:grid grid-cols-3 lg:gap-10">
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

    <div class="flex flex-col items-center gap-3 lg:w-[70%]">
      <div class="flex items-center gap-2 font-bold text-xl md:text-2xl">
        <h4>De categorieen</h4>
        <span v-if="categories && categories.length > 0">- {{ categories?.length }}</span>
      </div>
      <div v-if="!categoriesHasError">
        <!-- Skeleton -->
        <div v-if="loadingCategories" class="flex flex-wrap gap-2 justify-center">
          <span v-for="n in 14" :key="n" class="skeleton-text h-8 w-24 rounded-lg"></span>
        </div>

        <!-- Content -->
        <div v-else class="flex flex-wrap gap-2 text-sm justify-center md:text-base">
          <span
            v-for="category in categories"
            :key="category"
            class="bg-background p-2 px-4 shadow-lg rounded-lg"
          >
            {{ category }}
          </span>
        </div>
      </div>

      <span v-else class="bg-background p-4 px-6 rounded-lg text-primary"
        >Er konden geen categorieen gevonden worden!</span
      >
    </div>

    <div class="w-[80%] flex flex-col items-center gap-3">
      <div class="flex items-center gap-2 font-bold text-xl md:text-2xl ">
        <h4>Deelnemende partijen</h4>
        <span v-if="parties && parties.length > 0">- {{ parties?.length }}</span>
      </div>
      <div v-if="!partiesHasError">
        <!-- Skeleton -->
        <div v-if="loadingParties" class="flex flex-wrap gap-2 justify-center">
          <span v-for="n in 14" :key="n" class="skeleton-text h-8 w-32 rounded-lg"></span>
        </div>

        <!-- Content -->
        <div v-else class="flex flex-wrap gap-2 justify-center text-sm md:text-base">
          <span v-for="party in parties" :key="party.id" class="bg-background p-2 px-4 rounded-lg">
            {{ party.name }}
          </span>
        </div>
      </div>

      <span v-else class="bg-background p-4 px-6 rounded-lg text-primary"
        >Er konden geen partijen gevonden worden!</span
      >
    </div>
  </div>
</template>

<style scoped>
.skeleton-img,
.skeleton-text {
  background: linear-gradient(90deg, #3e3e3e 0%, #555 50%, #3e3e3e 100%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.2s infinite;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>
