<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getParties } from '@/services/PartyDetailService'

const route = useRoute()
const partyName = ref<string>('')
const routeName = route.params.name as string

onMounted(async () => {
  try {
    const data = await getParties(2023)
    const foundParty = data.affiliations.find(
      (p) => p.name.toLowerCase() === routeName.toLowerCase()
    )
    partyName.value = foundParty?.name ?? routeName
  } catch (err) {
    console.error('Kon partijen niet ophalen:', err)
    partyName.value = routeName
  }
})
</script>

<template>
  <div class="min-h-screen text-white">
    <header class="flex flex-col md:flex-row justify-between items-center px-8 py-16 gap-8">
      <div>
        <div class="flex items-center gap-3">
          <!--          <img-->
          <!--            src="@/assets/d66-logo.png"-->
          <!--            alt="Partij logo"-->
          <!--            class="h-20 w-auto rounded-lg"-->
          <!--          />-->
          <h2 class="text-6xl font-semibold">Partij {{ partyName }}</h2>
        </div>

        <p class="text-gray-300 mt-3 text-xl md:text-2xl">
          Ontdek de standpunten van Partij <span class="font-bold text-white"> {{ partyName }}</span>
        </p>
      </div>

      <div class="flex justify-center md:justify-end mt-8 md:mt-0">
        <img
          src="@/assets/detail-illustratie.png"
          alt="partij illustratie"
          class="w-40 md:w-56 lg:w-64 object-contain scale-130"
        />
      </div>
    </header>

    <section class="bg-[#131a2c] px-8 py-12">
      <h3 class="text-4xl font-semibold mb-4">Over {{ partyName }}</h3>
      <p class="text-gray-300 max-w-4xl leading-relaxed">
        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut
        labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi
        ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum
        dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
      </p>
    </section>
  </div>
</template>

<style scoped></style>
