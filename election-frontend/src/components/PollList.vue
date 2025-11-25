<script setup lang="ts">
import PollCard from "@/components/PollCard.vue"

const poll = {
  id: "p1",
  question: "De overheid moet meer investeren in onderwijs."
}
</script>

<template>
  <section class="w-full max-w-4xl mx-auto px-6 mt-16 mb-20">
    <h2 class="text-4xl font-bold text-center mb-3 text-white">
      Stelling van de dag
    </h2>

    <p class="text-text-muted text-center mb-8 text-lg">
      Laat je stem horen en zie direct hoe anderen denken.
    </p>

    <PollCard :poll="poll" />
  </section>
</template>
