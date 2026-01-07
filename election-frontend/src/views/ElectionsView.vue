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

const compareYear = ref<string>("2023")

const constituencies = ref<Constituency[]>([])
let municipalities: string[] = [];

// Separate data stores for comparison year
const compareConstituencies = ref<Constituency[]>([])
let compareMunicipalities: string[] = [];

const levels: string[] = ["Gemeente", "Kieskring"]
const selectedLevel = ref<string>("Kieskring");

const compareMode = ref<boolean>(false);
const compareDataError = ref<string | null>(null);

const selectedMunicipality = ref<Municipality | null>(null);
const selectedConstituency = ref<Constituency | null>(null);
const compareMunicipality = ref<Municipality | null>(null);
const compareConstituency = ref<Constituency | null>(null)

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
      compareConstituency.value = getCompareConstituencyByName(val);
    } else if (selectedLevel.value === "Gemeente") {
      try {
        compareMunicipality.value = await getMunicipalityData(compareYear.value, val)
        compareDataError.value = null;
      } catch (e) {
        compareDataError.value = `Geen data beschikbaar voor ${val} in ${compareYear.value}`;
        compareMunicipality.value = null;
      }
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

// Helper to get constituency from compare data
function getCompareConstituencyByName(name: string): Constituency | null {
  for (const constituency of compareConstituencies.value) {
    if (constituency.name === name) return constituency
  }
  return null
}

// Calculate percentage difference
function calcPercentageDiff(current: number, previous: number): number {
  if (previous === 0) return current > 0 ? 100 : 0;
  return ((current - previous) / previous) * 100;
}

// Constituency
const setChartData = () => {
  // Ensure we have valid constituency for selected year
  if (!selectedConstituency.value) {
    selectedConstituency.value = getConstituencyByName('Amsterdam');
  }
  if (!selectedConstituency.value) return null;

  if (selectedLevel.value === "Kieskring") selectedMunicipality.value = null;

  // Get primary data
  let primaryParties: Party[] | PartyResult[];
  if (selectedMunicipality.value == null) {
    primaryParties = [...selectedConstituency.value.parties].sort((a, b) => b.votes - a.votes)
  } else {
    primaryParties = [...selectedMunicipality.value.parties].sort((a, b) => b.votes - a.votes)
  }

  const labels = primaryParties.map((p) => p.name)
  const primaryValues = primaryParties.map((p) => p.votes)

  // Single year mode - vivid colors
  const primaryColors = labels.map((_, i) => palette[i % palette.length])

  if (compareMode.value && compareConstituencies.value.length > 0) {
    // Get compare data
    if (!compareConstituency.value) {
      compareConstituency.value = getCompareConstituencyByName(selectedConstituency.value?.name ?? 'Amsterdam');
    }

    let compareParties: Party[] | PartyResult[] = [];
    if (compareMunicipality.value == null && compareConstituency.value) {
      compareParties = [...compareConstituency.value.parties]
    } else if (compareMunicipality.value) {
      compareParties = [...compareMunicipality.value.parties]
    }

    // Create a map for quick lookup of compare values
    const compareMap = new Map<string, number>();
    compareParties.forEach(p => compareMap.set(p.name, p.votes));

    // Get compare values in same order as primary labels
    const compareValues = labels.map(name => compareMap.get(name) ?? 0);

    // Calculate differences for styling
    const differences = primaryValues.map((val, i) => {
      const compareVal = compareValues[i];
      return val - compareVal;
    });

    // Create softer/semi-transparent colors for compare year (older data)
    const compareColors = labels.map((_, i) => {
      const baseColor = palette[i % palette.length];
      // Make it more transparent for the comparison bars
      return baseColor + '80'; // 50% opacity
    });

    return {
      labels,
      datasets: [
        {
          label: `${selectedYear.value}`,
          data: primaryValues,
          backgroundColor: primaryColors,
          hoverBackgroundColor: primaryColors,
          borderRadius: 6,
          borderSkipped: false,
          minBarLength: 2,
          barPercentage: 0.8,
          categoryPercentage: 0.9,
        },
        {
          label: `${compareYear.value}`,
          data: compareValues,
          backgroundColor: compareColors,
          hoverBackgroundColor: compareColors,
          borderRadius: 6,
          borderSkipped: false,
          minBarLength: 2,
          barPercentage: 0.8,
          categoryPercentage: 0.9,
        }
      ],
      _differences: differences, // Store for tooltip access
    } as ChartData<'bar'> & { _differences?: number[] };
  }

  return {
    labels,
    datasets: [
      {
        label: `${selectedYear.value}`,
        data: primaryValues,
        backgroundColor: primaryColors,
        hoverBackgroundColor: primaryColors,
        borderRadius: 8,
        borderSkipped: false,
        minBarLength: 2,
      },
    ],
  }
}

const setChartOptions = (): ChartOptions<'bar'> => ({
  indexAxis: compareMode.value ? 'x' : 'y', // Vertical bars in compare mode
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 500,
    easing: 'easeOutQuart',
  },
  layout: {
    padding: { left: 4, right: 8, top: 4, bottom: compareMode.value ? 60 : 4 },
  },
  plugins: {
    legend: {
      display: compareMode.value,
      position: 'top',
      align: 'end',
      labels: {
        color: '#e2e8f0',
        usePointStyle: true,
        pointStyle: 'rect',
        padding: 16,
        font: {
          size: 12,
          weight: 500,
        },
      },
    },
    tooltip: {
      backgroundColor: '#1e293b',
      titleColor: '#f1f5f9',
      bodyColor: '#e2e8f0',
      borderColor: 'rgba(255,255,255,0.1)',
      borderWidth: 1,
      padding: 12,
      displayColors: true,
      callbacks: {
        afterBody: function(context) {
          if (!compareMode.value || context.length < 1) return '';
          
          const dataIndex = context[0].dataIndex;
          const datasetIndex = context[0].datasetIndex;
          const chart = context[0].chart;
          const datasets = chart.data.datasets;
          
          if (datasets.length < 2) return '';
          
          // Get the value of the hovered bar and the other bar
          const hoveredVal = Number(datasets[datasetIndex].data[dataIndex]) || 0;
          const otherVal = Number(datasets[datasetIndex === 0 ? 1 : 0].data[dataIndex]) || 0;
          const otherYear = datasetIndex === 0 ? compareYear.value : selectedYear.value;
          
          const diff = hoveredVal - otherVal;
          const percentDiff = otherVal > 0 ? ((diff / otherVal) * 100).toFixed(1) : '∞';
          
          const arrow = diff > 0 ? '↑' : diff < 0 ? '↓' : '→';
          const sign = diff > 0 ? '+' : '';
          
          return `\nt.o.v. ${otherYear}: ${sign}${diff.toLocaleString('nl-NL')} stemmen (${sign}${percentDiff}%) ${arrow}`;
        }
      }
    },
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: {
        color: '#e2e8f0',
        font: { weight: 500, size: compareMode.value ? 10 : 12 },
        maxRotation: compareMode.value ? 45 : 0,
        callback: function(value) {
          if (!compareMode.value) return value;
          // Truncate long party names for vertical chart
          const label = this.getLabelForValue(value as number);
          if (typeof label === 'string' && label.length > 12) {
            return label.substring(0, 12) + '...';
          }
          return label;
        }
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

const chartOptions = ref<ChartOptions<'bar'>>(setChartOptions())

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

// fetch constituencies for selected year
const fetchData = async () => {
  try {
    constituencies.value = await getConstituencies(selectedYear.value)
    municipalities = await getMunicipalities(selectedYear.value)
    // Set default constituency after fetching
    if (!selectedConstituency.value || !getConstituencyByName(selectedConstituency.value.name)) {
      selectedConstituency.value = getConstituencyByName('Amsterdam');
    }
  } catch (e) {
    console.error('Failed to fetch constituencies', e)
  }
}

// fetch constituencies for compare year
const fetchCompareData = async () => {
  if (!compareMode.value) return;
  
  try {
    compareConstituencies.value = await getConstituencies(compareYear.value)
    compareMunicipalities = await getMunicipalities(compareYear.value)
    compareDataError.value = null;
    
    // Set compare constituency to match selected
    const matchingConstituency = getCompareConstituencyByName(
      selectedConstituency.value?.name ?? 'Amsterdam'
    );
    
    if (matchingConstituency) {
      compareConstituency.value = matchingConstituency;
    } else {
      compareDataError.value = `Kieskring "${selectedConstituency.value?.name}" niet gevonden in ${compareYear.value}`;
    }
    
    // Also fetch compare municipality if needed
    if (selectedLevel.value === "Gemeente" && selectedMunicipality.value) {
      try {
        compareMunicipality.value = await getMunicipalityData(
          compareYear.value, 
          selectedMunicipality.value.name
        );
      } catch (e) {
        compareDataError.value = `Gemeente "${selectedMunicipality.value.name}" niet gevonden in ${compareYear.value}`;
        compareMunicipality.value = null;
      }
    }
  } catch (e) {
    console.error('Failed to fetch compare constituencies', e)
    compareDataError.value = `Geen data beschikbaar voor ${compareYear.value}`;
  }
}

onMounted(async () => {
  years.value = await getElectionYears();
  await fetchData()
  chartData.value = setChartData()
  chartOptions.value = setChartOptions()
})

// Update chart when user changes selection
watch([selectedConstituency, selectedMunicipality, selectedLevel, compareConstituency, compareMunicipality], async () => {
  if (!constituencies.value.length) return
  if (selectedLevel.value === "Gemeente" && selectedMunicipality.value === null) {
    selectedMunicipality.value = await getMunicipalityData(selectedYear.value, "Amsterdam")
  }
  chartData.value = setChartData()
  chartOptions.value = setChartOptions()
})

// Watch selected year - refetch primary data
watch(selectedYear, async () => {
  await fetchData()
  chartData.value = setChartData()
  chartOptions.value = setChartOptions()
})

// Watch compare mode toggle
watch(compareMode, async (newVal) => {
  if (newVal) {
    await fetchCompareData()
  } else {
    compareConstituencies.value = []
    compareConstituency.value = null;
    compareMunicipality.value = null;
    compareDataError.value = null;
  }
  chartData.value = setChartData()
  chartOptions.value = setChartOptions()
})

// Watch compare year - refetch compare data
watch(compareYear, async () => {
  if (compareMode.value) {
    await fetchCompareData()
    chartData.value = setChartData()
    chartOptions.value = setChartOptions()
  }
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
                <template v-if="compareMode">
                  {{ selectedMunicipality?.name || selectedConstituency?.name || '-' }} ({{ selectedYear }})
                  <span class="text-white/60 font-normal">vs</span>
                  {{ compareMunicipality?.name || compareConstituency?.name || '-' }} ({{ compareYear }})
                </template>
                <template v-else>
                  Uitslag per partij: {{ selectedMunicipality?.name || selectedConstituency?.name || '-' }}
                </template>
              </h3>
              <span class="text-xs text-white/60">Stemmen</span>
            </div>
            <!-- Error message -->
            <div v-if="compareDataError" class="mx-4 mt-3 p-3 bg-amber-500/20 border border-amber-500/40 rounded-lg">
              <p class="text-amber-200 text-sm">⚠️ {{ compareDataError }}</p>
            </div>
            <ChartComponent :chartData="chartData" :chartOptions="chartOptions" />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
