<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'

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

const currentWidth = ref(0);

onMounted(() => {
  requestAnimationFrame(() => {
    currentWidth.value = calculatedWidthPercentage.value;
  });
});

watch(() => calculatedWidthPercentage, (val) => {
  currentWidth.value = val.value;
});
</script>

<template>
  <div class="w-full h-2 bg-white rounded-3xl">
    <div
      class="h-full rounded-3xl bg-primary transition-all duration-500 ease-out"
      :style="{ width: currentWidth + '%' }"
    ></div>
  </div>
</template>

<style scoped></style>
