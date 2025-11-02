<script setup lang="ts">
defineProps<{
  title?: string
  data: { name: string; color: string; value: number }[]
}>()
</script>

<template>
  <p>Party: {{ title }}</p>
  <p>Votes: {{ data[0].value }}</p>
</template>
