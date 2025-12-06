<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
  totalAnswered: Number,
  totalStatements: Number,
  percentage: Number,
})
const calculatedWidthPercentage = computed(() => {
  if (props.percentage !== undefined) {
    return props.percentage
  } else {
    return props.totalAnswered && props.totalStatements
      ? (props.totalAnswered / props.totalStatements) * 100
      : 0
  }
})
</script>

<template>
  <div class="w-full h-2 bg-white rounded-3xl">
    <div
      class="h-full rounded-3xl bg-primary transition-all duration-500"
      :style="{ width: calculatedWidthPercentage + '%' }"
    ></div>
  </div>
</template>

<style scoped></style>
