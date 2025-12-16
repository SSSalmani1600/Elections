<script setup lang="ts">
import type { ChartData, ChartOptions } from 'chart.js';
import Chart from 'primevue/chart'
const { chartData, chartOptions } = defineProps<{
  chartData: ChartData<'bar'> | null;
  chartOptions?: ChartOptions<'bar'>;
}>()

</script>

<template>
  <div class="p-3 h-[360px] sm:h-[420px] md:h-[460px] lg:h-[520px]">
    <Chart v-if="chartData" type="bar" :data="chartData" :options="chartOptions" class="w-full h-full" />
    <div v-else class="flex items-center justify-center w-full h-full">
      <p class="text-center text-white/80">Couldn't load data</p>
    </div>
  </div>
</template>
