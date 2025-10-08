<script setup lang="ts">
import TheWelcome from '../components/TheWelcome.vue'
</script>

<template>
  <main class="flex flex-col items-center">
    <div class="flex flex-col items-center w-fit mt-[75px] gap-8">
      <div class="flex flex-col items-center gap-3">
      <h1 class="text-4xl font-bold">Maak kennis met de Nederlandse politiek</h1>
      <p class="text-text-muted text-lg w-[60%] text-center"><strong>Leer</strong> partijen kennen, <strong>bekijk</strong> de uitslagen en <strong>ontdek</strong> jouw politieke match</p>
      </div>
      <div class="flex gap-4">
        <button class="btn btn-primary">Ontdek verkiezingen</button>
        <button class="btn btn-secondary">Doe de match quiz</button>
      </div>
    </div>
  </main>
</template>
