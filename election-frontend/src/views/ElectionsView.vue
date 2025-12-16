<script setup lang="ts">
import { getConstituencies, getElectionYears, getMunicipalities, getMunicipalityData } from '@/services/ElectionService'
import { type Municipality, type Constituency, type Party, type PartyResult } from '@/types/api'
import { ref, onMounted, watch, computed } from 'vue'
import { type ChartData, type ChartOptions } from 'chart.js'
import MunicipalitiesMap from '@/components/maps/MunicipalitiesMap.vue'
import ChartComponent from '@/components/ChartComponent.vue'
import VoteDropdown from '@/components/VoteDropdown.vue'
import { Map as MapIcon, Layers } from 'lucide-vue-next';
import { ArrowRightLeft } from 'lucide-vue-next'

const years = ref<number[]>([])
const selectedYear = ref<string>("2025")

const compareYear = ref<string>("2025")

const constituencies = ref<Constituency[]>([])
let municipalities: string[] = [];

const levels: string[] = ["Gemeente", "Kieskring"]
const selectedLevel = ref<string>("Kieskring");

const compareMode = ref<boolean>(false);

const selectedMunicipality = ref<Municipality | null>(null);
const selectedConstituency = ref<Constituency | null>(getConstituencyByName("Amsterdam"));
const compareMunicipality = ref<Municipality | null>(null);
const compareConstituency = ref<Constituency | null>(getConstituencyByName("Amsterdam"))

const compareDropdownValue = computed({
  get() {
    if (selectedLevel.value === "Kieskring") {
      return compareConstituency.value?.name ?? "Amsterdam";
    } else if (selectedLevel.value === "Gemeente") {
      return compareMunicipality.value?.name ?? "Amsterdam";
    }
    return "Amsterdam";
  },
  async set(val: string) {
    if (selectedLevel.value === "Kieskring") {
      compareConstituency.value = getConstituencyByName(val);
    } else if (selectedLevel.value === "Gemeente") {
      compareMunicipality.value = await getMunicipalityData(selectedYear.value, val)
    }
  }
})

const selectedDropdownValue = computed({
  get() {
    if (selectedLevel.value === "Kieskring") {
      return selectedConstituency.value?.name ?? "Amsterdam";
    } else if (selectedLevel.value === "Gemeente") {
      return selectedMunicipality.value?.name ?? "Amsterdam";
    }
    return "Amsterdam";
  },
  set(val: string) {
    if (selectedLevel.value === "Kieskring") {
      selectedConstituency.value = getConstituencyByName(val);
    } else if (selectedLevel.value === "Gemeente") {
      onMapSelect(val)
    }
  }
});

const dropdownOptions = computed(() => {
  if (selectedLevel.value === "Kieskring") {
    return constituencies.value.map(c => c.name)
  } else if (selectedLevel.value === "Gemeente") {
    return [...municipalities].sort((a, b) => a.localeCompare(b));
  }
  return [];
});

const chartData = ref<ChartData<'bar'> | null>(null)

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

async function onMapSelect(municipalityName: string) {
  try {
    const data = await getMunicipalityData(selectedYear.value, municipalityName);

    if (!data) return;

    if (!selectedConstituency.value) return;

    if (selectedConstituency.value.constituencyId !== data.constituencyId) {
      selectedConstituency.value.constituencyId = data.constituencyId;
    }

    selectedLevel.value = "Gemeente"

    selectedMunicipality.value = data;
  } catch (error) {
    console.error("Error in map selection:", error);
  }
}

// Constituency
const setChartData = () => {
  selectedConstituency.value = getConstituencyByName(
    selectedConstituency.value?.name ?? 'Amsterdam',
  )!

  if (selectedLevel.value === "Kieskring") selectedMunicipality.value = null;

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

  if (compareMode.value) {
    compareConstituency.value = getConstituencyByName(
      compareConstituency.value?.name ?? 'Amsterdam',
    )!
    if (compareMunicipality.value == null) {
      sorted = [...compareConstituency.value.parties].sort((a, b) => b.votes - a.votes)
    } else {
      sorted = [...compareMunicipality.value.parties].sort((a, b) => b.votes - a.votes)
    }

    const values2 = sorted.map((p) => p.votes)

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
          minBarLength: 2,
        },
        {
          label: 'Stemmen',
          data: values2,
          backgroundColor: colors,
          hoverBackgroundColor: colors,
          borderRadius: 8,
          borderSkipped: false,
          minBarLength: 2,
        }
      ],
    }
  }

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
        minBarLength: 2,
      },
    ],
  }
}

const setChartOptions = (): ChartOptions<'bar'> => ({
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
      grid: { display: false },
      ticks: {
        color: '#e2e8f0',
        font: { weight: 500 },
      },
    },
    y: {
      grid: { display: false },
      ticks: {
        autoSkip: false,
        color: '#cbd5e1',
        padding: 6,
      },
    },
  },
})

const chartOptions: ChartOptions<'bar'> = setChartOptions()

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
const fetchData = async () => {
  try {
    constituencies.value = await getConstituencies(selectedYear.value)
    municipalities = await getMunicipalities(selectedYear.value)
  } catch (e) {
    console.error('Failed to fetch constituencies', e)
  }
}

onMounted(async () => {
  years.value = await getElectionYears();
  await fetchData()

  chartData.value = setChartData()
})

// Update chart when user changes selection
watch([selectedConstituency, selectedMunicipality, selectedLevel, compareMode], async () => {
  if (!constituencies.value.length) return
  if (selectedLevel.value === "Gemeente" && selectedMunicipality.value === null) {
    selectedMunicipality.value = await getMunicipalityData(selectedYear.value, "Amsterdam")
  }
  chartData.value = setChartData()
})
watch(selectedYear, async () => {
  await fetchData()
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
      <div class="flex flex-col sm:items-stretch sm:items-center gap-4">
        <div class="flex gap-4 relative flex-wrap">
          <VoteDropdown v-model="selectedYear" :options="years" label="Jaar">
            <template #icon>
              <span class="font-bold text-xs">JR</span>
            </template>
          </VoteDropdown>
          <VoteDropdown v-model="selectedLevel" :options="levels" label="Niveau">
            <template #icon>
              <Layers :size="14" />
            </template>
          </VoteDropdown>
          <VoteDropdown v-model="selectedDropdownValue" :options="dropdownOptions" :label="selectedLevel">
            <template #icon>
              <MapIcon :size="14" />
            </template>
          </VoteDropdown>
          <button @click="compareMode = !compareMode" :class="[
            'flex cursor-pointer items-center gap-2 px-4 py-2.5 rounded-lg border transition-all duration-300 w-full md:w-auto justify-center',
            compareMode
              ? 'bg-blue-600/20 border-blue-500/50 text-blue-200'
              : 'bg-slate-800 border-slate-700 text-slate-400 hover:text-white hover:border-slate-500'
          ]">
            <ArrowRightLeft :size="16" />
            <span class="text-sm font-medium">Vergelijken</span>
            <span v-if="compareMode" class="ml-1 w-2 h-2 rounded-full bg-blue-400 animate-pulse"></span>
          </button>
        </div>
        <!-- Comparison Section -->
        <div v-if="compareMode"
          class="
          mt-4 p-4 bg-slate-800/50 rounded-lg border border-slate-700 flex flex-col gap-3 animate-in fade-in slide-in-from-top-4">
          <span className="text-xs font-bold text-slate-400 uppercase">Vergelijk met:</span>
          <div className="flex gap-3 flex-wrap">
            <VoteDropdown v-model="compareYear" :options="years" label="Jaar">
              <template #icon>
                <span class="font-bold text-xs">JR</span>
              </template>
            </VoteDropdown>
            <VoteDropdown v-model="selectedLevel" :options="levels" label="Niveau">
              <template #icon>
                <Layers :size="14" />
              </template>
            </VoteDropdown>
            <VoteDropdown v-model="compareDropdownValue" :options="dropdownOptions" :label="selectedLevel">
              <template #icon>
                <MapIcon :size="14" />
              </template>
            </VoteDropdown>
          </div>
        </div>
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
            <ChartComponent :chartData="chartData" :chartOptions="chartOptions" />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
