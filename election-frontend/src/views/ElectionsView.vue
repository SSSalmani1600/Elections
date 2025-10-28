<script setup lang="ts">
import { getConstituencies } from '@/services/ElectionService'
import type { Constituency, Party } from '@/types/api'
import { ref, onMounted, watch } from 'vue'
import Select from 'primevue/select'
import Chart from 'primevue/chart'
import MunicipalitiesMap from '@/components/maps/MunicipalitiesMap.vue'

const constituencies = ref<Constituency[]>([])

const chartData = ref()
const chartOptions = ref()

//
const selectedConstituency = ref<string | null>(null)
const setChartData = () => {
  const currentConstituency: Constituency | null = getConstituencyByName(
    selectedConstituency.value ?? 'Amsterdam',
  )!

  const sorted: Party[] = [...currentConstituency.parties].sort((a, b) => b.votes - a.votes)

  return {
    labels: sorted.map((p) => p.name),
    datasets: [
      {
        label: 'Votes',
        labelBackground: '#EF3054',
        barThickness: 12,
        data: sorted.map((p) => p.votes),
      },
    ],
  }
}

const setChartOptions = () => ({
  indexAxis: 'y',
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { labels: { color: 'white' } } },
  scales: {
    x: {
      ticks: { color: 'white', font: { weight: 500 } },
      grid: { display: false, drawBorder: false },
    },
    y: { ticks: { color: 'white' }, grid: { display: false } },
  },
})

// years to pick from
// const years = [2023, 2021, 2019] as const
// type Year = (typeof years)[number]
// const selectedYear = ref<Year>(2023)

function scrollToSection(id: string) {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
  }
}

function getConstituencyByName(name: string): Constituency | null {
  for (const constituency of constituencies.value) {
    if (constituency.name === name) return constituency
  }
  return null
}

// fetch constituencies
const fetchConstituencies = async () => {
  try {
    constituencies.value = await getConstituencies()
  } catch (e) {
    console.error('Failed to fetch constituencies', e)
  }
}

onMounted(async () => {
  await fetchConstituencies()

  if (!selectedConstituency.value && constituencies.value.length) {
    selectedConstituency.value = constituencies.value[0].name
  }

  chartOptions.value = setChartOptions()
  chartData.value = setChartData()
})

// Update chart when user changes selection
watch(selectedConstituency, () => {
  if (!constituencies.value.length) return
  chartData.value = setChartData()
})
</script>

<template>
  <section class="text-white px-6 py-16 md:py-24">
    <div class="max-w-[100rem] mx-auto grid md:grid-cols-2 items-center gap-12 lg:gap-20">
      <div>
        <h1 class="text-4xl sm:text-5xl lg:text-6xl xl:text-6xl font-extrabold leading-tight tracking-tight">
          Ontdek de verkiezingen<br />
          in Nederland
        </h1>

        <p class="mt-6 text-lg sm:text-xl lg:text-2xl text-white/85 max-w-xl leading-relaxed">
          Bekijk wanneer verkiezingen plaatsvinden, leer over de thema’s en zie de resultaten van
          eerdere verkiezingen.
        </p>

        <div class="mt-8 flex flex-wrap gap-4">
          <button type="button" @click="scrollToSection('komende-verkiezingen')"
            class="bg-[#EF3054] cursor-pointer font-semibold px-6 py-4 rounded-2xl text-base sm:text-lg shadow-md hover:shadow-lg hover:bg-[#d11f45] transition">
            Komende verkiezingen
          </button>
          <button type="button"
            class="bg-white text-black cursor-pointer font-semibold px-6 py-4 rounded-2xl text-base sm:text-lg shadow hover:bg-gray-100 transition">
            Uitslagen voorgaande jaren
          </button>
        </div>
      </div>

      <div class="flex justify-center md:justify-end">
        <img src="/src/assets/img/elections-image.png" alt="Illustratie van verkiezingen"
          class="w-full max-w-lg lg:max-w-xl xl:max-w-2xl" />
      </div>
    </div>
  </section>

  <section id="komende-verkiezingen" class="px-6 py-16 md:py-20 text-white bg-[#0B132B]">
    <div class="max-w-7xl mx-auto">
      <h2 class="text-2xl sm:text-3xl font-semibold mb-10">Komende verkiezingen</h2>

      <div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        <div class="bg-white/5 border border-white/10 rounded-lg p-6 hover:bg-white/10 transition">
          <h3 class="text-lg font-semibold mb-2">22 november 2025</h3>
          <p class="text-sm text-white/70">
            Lorem ipsum dolor sit amet. Vel dignissimos nihil sit dignissimos.
          </p>
        </div>

        <div class="bg-white/5 border border-white/10 rounded-lg p-6 hover:bg-white/10 transition">
          <h3 class="text-lg font-semibold mb-2">Gemeenteraadsverkiezingen</h3>
          <p class="text-sm text-white/70">
            Lorem ipsum dolor sit amet. Vel dignissimos nihil sit dignissimos.
          </p>
        </div>

        <div class="bg-white/5 border border-white/10 rounded-lg p-6 hover:bg-white/10 transition">
          <h3 class="text-lg font-semibold mb-2">Provinciale Statenverkiezingen</h3>
          <p class="text-sm text-white/70">
            Lorem ipsum dolor sit amet. Vel dignissimos nihil sit dignissimos.
          </p>
        </div>

        <div class="bg-white/5 border border-white/10 rounded-lg p-6 hover:bg-white/10 transition">
          <h3 class="text-lg font-semibold mb-2">Europees parlementsverkiezingen</h3>
          <p class="text-sm text-white/70">
            Lorem ipsum dolor sit amet. Vel dignissimos nihil sit dignissimos.
          </p>
        </div>
      </div>
    </div>
  </section>
  <!--- Graph Section --->
  <section class="px-6 py-12 text-white bg-[#0B132B]">

    <!-- Filter -->
    <div class="flex justify-center p-8">
      <div class="flex gap-4">
        <Select v-model="selectedConstituency" name="Kieskringen" :options="constituencies" optionLabel="name"
          optionValue="name" placeholder="Selecteer een kieskring" fluid />
        <!-- <Select name="Partijen" :options="years" optionLabel="" placeholder="Partij" fluid /> -->
      </div>
    </div>

    <div class="flex flex-row-reverse">
      <!-- Municipalities Map -->
      <div>
        <MunicipalitiesMap v-model:selectedConstituency="selectedConstituency" />
      </div>
      <!-- Graph -->
      <div class="mb-16 w-full max-w-4xl mx-auto h-[70vh] sm:h-[65vh] min-h-[65vh]">
        <Chart type="bar" :data="chartData" :options="chartOptions" class="w-full h-full" />
      </div>
    </div>
  </section>

  <!-- Parties with year selector -->
  <!-- <section class="px-6 py-16 text-white">
    <div class="max-w-7xl mx-auto">
      <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4 mb-8">
        <div class="flex items-end gap-3">
          <h2 class="text-2xl sm:text-3xl font-semibold">Deelnemende partijen</h2>
          <span v-if="parties" class="text-white/60 text-sm hidden sm:inline">
            {{ parties.affiliations.length }} partijen
          </span>
        </div>

         Year selector
        <div class="flex items-center gap-2">
          <label for="year" class="text-sm text-white/70">Jaar</label>
          <select
            id="year"
            v-model.number="selectedYear"
            class="bg-white/5 border border-white/15 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-white/30"
          >
            <option class="text-black" v-for="y in years" :key="y" :value="y">{{ y }}</option>
          </select>
        </div>
      </div>

      Loading skeleton
      <div v-if="loading" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        <div
          v-for="i in 8"
          :key="i"
          class="bg-white/5 border border-white/10 rounded-xl p-5 animate-pulse"
        >
          <div class="flex items-center gap-4">
            <div class="h-10 w-10 rounded-full bg-white/10"></div>
            <div class="flex-1 space-y-2">
              <div class="h-3 w-2/3 bg-white/10 rounded"></div>
              <div class="h-3 w-1/3 bg-white/10 rounded"></div>
            </div>
          </div>
        </div>
      </div>

      List
      <div v-else-if="parties" class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        <article
          v-for="aff in parties.affiliations"
          :key="aff.name"
          class="group bg-white/5 border border-white/10 rounded-xl p-5 transition hover:bg-white/10 hover:border-white/20"
        >
          <div class="flex items-center gap-4">
            <div
              class="shrink-0 h-10 w-10 rounded-full bg-white/10 grid place-items-center font-semibold"
              aria-hidden="true"
              :title="aff.name"
            >
              {{ aff.name?.charAt(0)?.toUpperCase() }}
            </div>

            <div class="min-w-0">
              <h3 class="font-semibold leading-tight truncate">{{ aff.name }}</h3>
              <p class="text-xs text-white/60">Politieke partij</p>
            </div>

            <svg
              class="ml-auto opacity-0 group-hover:opacity-100 transition h-5 w-5 text-white/60"
              viewBox="0 0 20 20"
              fill="currentColor"
              aria-hidden="true"
            >
              <path
                fill-rule="evenodd"
                d="M10.293 3.293a1 1 0 011.414 0l5 5a1 1 0 010 1.414l-5 5a1 1 0 11-1.414-1.414L13.586 11H4a1 1 0 110-2h9.586l-3.293-3.293a1 1 0 010-1.414z"
                clip-rule="evenodd"
              />
            </svg>
          </div>
        </article>
      </div>

      <p v-else class="text-white/70">Geen partijen gevonden.</p>
    </div>
  </section> -->
</template>
