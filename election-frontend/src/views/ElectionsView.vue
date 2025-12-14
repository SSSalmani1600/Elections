<script setup lang="ts">
import { getConstituencies, getElectionYears, getMunicipalityData } from '@/services/ElectionService'
import { type Municipality, type Constituency, type Party, type PartyResult } from '@/types/api'
import { ref, onMounted, watch, computed } from 'vue'
import Select from 'primevue/select'
import Chart from 'primevue/chart'
import MunicipalitiesMap from '@/components/maps/MunicipalitiesMap.vue'

const constituencies = ref<Constituency[]>([])
const years = ref<number[]>([])
const selectedYear = ref<number>(2025)
const yearOptions = computed(() =>
  [...years.value]
    .map((y) => ({
      label: y,
      value: y,
    }))
)

const constituencyOptions = computed(() =>
  [...constituencies.value]
    .map((c) => ({
      label: c.name,
      value: c.constituencyId,
    }))
    .sort((a, b) => a.label.localeCompare(b.label))
)

const chartData = ref()
const chartOptions = ref()

const palette: string[] = [
  '#60A5FA', // blue-400
  '#A78BFA', // violet-400
  '#34D399', // emerald-400
  '#FBBF24', // amber-400
  '#F472B6', // pink-400
  '#38BDF8', // sky-400
  '#F59E0B', // amber-500
  '#4ADE80', // green-400
  '#C084FC', // violet-300
  '#F87171', // red-400
]

const selectedMunicipality = ref<Municipality | null>(null);
const selectedConstituencyId = ref<string | null>(null)
const selectedConstituency = ref<Constituency | null>(null);

async function onMapSelect(municipalityName: string) {
  try {
    const data = await getMunicipalityData(selectedYear.value, municipalityName);

    if (!data) return;

    if (selectedConstituencyId.value !== data.constituencyId) {
      selectedConstituencyId.value = data.constituencyId;
    }

    selectedMunicipality.value = data;
  } catch (error) {
    console.error("Error in map selection:", error);
  }
}

// Constituency
const setChartData = () => {
  selectedConstituency.value = getConstituencyById(
    selectedConstituencyId.value ?? '9',
  )!

  let sorted: Party[] | PartyResult[];

  if (selectedMunicipality.value == null) {
    sorted = [...selectedConstituency.value.parties].sort((a, b) => b.votes - a.votes)
  } else {
    sorted = [...selectedMunicipality.value.parties].sort((a, b) => b.votes - a.votes)
  }

  const labels = sorted.map((p) => p.name)
  const values = sorted.map((p) => p.votes)

  // color per bar
  const colors = labels.map((_, i) => palette[i % palette.length])

  return {
    labels,
    datasets: [
      {
        label: 'Stemmen',
        data: values,
        backgroundColor: colors,
        hoverBackgroundColor: colors,
        borderRadius: 8,
        borderSkipped: false,
        barThickness: 14,
        maxBarThickness: 18,
        minBarLength: 2,
      },
    ],
  }
}

const setChartOptions = () => ({
  indexAxis: 'y',
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 500,
    easing: 'easeOutQuart',
  },
  layout: {
    padding: { left: 4, right: 8, top: 4, bottom: 4 },
  },
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      backgroundColor: '#e2e8f0',
      titleColor: '#0B132B',
      bodyColor: '#0B132B',
      borderColor: 'rgba(0,0,0,0.08)',
      borderWidth: 1,
      padding: 10,
      displayColors: false,
    },
  },
  scales: {
    x: {
      grid: { display: false, drawBorder: false },
      ticks: {
        color: '#e2e8f0',
        font: { weight: 500 },
      },
    },
    y: {
      grid: { display: false, drawBorder: false },
      ticks: {
        autoSkip: false,
        color: '#cbd5e1',
        padding: 6,
      },
    },
  },
  categoryPercentage: 0.85,
  barPercentage: 0.9,
})

function scrollToSection(id: string) {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
  }
}

function getConstituencyById(id: string): Constituency | null {
  for (const constituency of constituencies.value) {
    if (constituency.constituencyId === id) return constituency
  }
  return null
}

// fetch constituencies
const fetchConstituencies = async () => {
  try {
    constituencies.value = await getConstituencies(selectedYear.value)
  } catch (e) {
    console.error('Failed to fetch constituencies', e)
  }
}

onMounted(async () => {
  years.value = await getElectionYears();
  await fetchConstituencies()

  if (!selectedConstituency.value && constituencies.value.length) {
    selectedConstituency.value = constituencies.value.find(c => c.name === "Amsterdam")
      || constituencies.value[0];
  }

  chartOptions.value = setChartOptions()
  chartData.value = setChartData()
})

// Update chart when user changes selection
watch([selectedConstituencyId, selectedMunicipality], () => {
  if (!constituencies.value.length) return
  chartData.value = setChartData()
})
watch(selectedYear, async () => {
  await fetchConstituencies()
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
    <div class="max-w-7xl mx-auto space-y-8">
      <!-- Filter row -->
      <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-4">
        <Select v-model="selectedYear" name="Jaren" placeholder="Selecteer een jaar" :options="yearOptions"
          optionLabel="label" optionValue="value" />
        <Select v-model="selectedConstituencyId" name="Kieskringen" :options="constituencyOptions" optionLabel="label"
          optionValue="value" placeholder="Selecteer een kieskring" class="sm:max-w-md" />
      </div>

      <!-- Content grid -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 items-start">
        <!-- Map -->
        <div class="order-2 lg:order-1 h-full">
          <MunicipalitiesMap :year="selectedYear.toString()" @update:selectedMunicipality="onMapSelect" />
        </div>

        <!-- Chart card -->
        <div class="order-1 lg:order-2 h-full">
          <div class="w-full h-full rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm shadow-lg">
            <div class="px-4 py-3 border-b border-white/10 flex items-center justify-between">
              <h3 class="text-sm font-semibold text-white/90">
                Uitslag per partij: {{ selectedMunicipality?.name || selectedConstituency?.name || '-' }}
              </h3>
              <span class="text-xs text-white/60">Stemmen</span>
            </div>
            <div class="p-3 h-[360px] sm:h-[420px] md:h-[460px] lg:h-[520px]">
              <Chart v-if="chartData" type="bar" :data="chartData" :options="chartOptions" class="w-full h-full" />
              <div v-else class="flex items-center justify-center w-full h-full">
                <p class="text-center text-white/80">Couldn't load data</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
