<script setup lang="ts">
import {useRouter} from 'vue-router'

const router = useRouter()

const goToElections = () => router.push('/elections')
const goToQuiz = () => router.push('/quiz')
const goToForum = () => router.push('/discussions')
</script>

<template>
  <main
    class="flex flex-col items-center min-h-screen gap-16">
    <div class="flex flex-col items-center w-fit mt-[75px] gap-8">
      <div class="flex flex-col items-center gap-3">
        <h1 class="text-4xl font-bold">Maak kennis met de Nederlandse politiek</h1>
        <p class="text-text-muted text-lg w-[60%] text-center"><strong>Leer</strong> partijen
          kennen, <strong>bekijk</strong> de uitslagen en <strong>ontdek</strong> jouw politieke
          match</p>
      </div>
      <div class="flex gap-4">
        <button class="btn btn-primary">Ontdek verkiezingen</button>
        <button class="btn btn-secondary">Doe de match quiz</button>
      </div>
    </div>

    <footer
      class="w-full bg-background text-center py-16 flex items-center justify-center mt-auto gap-12">
      <div>
        <img src="../assets/img/forum-image.svg" class="w-[250px]" alt="Forum image">
      </div>
      <div class="flex flex-col gap-6">
        <div class="flex flex-col items-center gap-3">
          <h2 class="text-2xl font-bold">Praat mee!</h2>
          <p class="text-text-muted px-6 leading-relaxed max-w-[500px]">
            In ons forum kun je jouw mening delen met anderen en ontdekken wat zij denken.
            Je vindt er bovendien handige informatie en nieuwe perspectieven.
          </p>
        </div>
        <button @click="goToForum" class="btn btn-primary w-fit m-auto">
          Ga naar het forum
        </button>
      </div>
    </footer>
  </main>
</template>
