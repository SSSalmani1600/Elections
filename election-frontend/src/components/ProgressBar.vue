<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
  totalAnswered: Number,
  totalStatements: Number,
})
const calculatedWidthPercentage = computed(() => {
  return props.totalAnswered && props.totalStatements ? (props.totalAnswered / props.totalStatements) * 100 : 0
})
</script>

<template>
  <div class="w-full h-2 bg-white rounded-3xl">
    <div class="h-2 rounded-3xl bg-primary transition-all duration-500" :style="{width: calculatedWidthPercentage + '%'}"></div>
  </div>
</template>

<style scoped></style>
