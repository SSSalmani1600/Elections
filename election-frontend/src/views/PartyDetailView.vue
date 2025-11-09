<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getParties } from '@/services/ElectionService.ts'
import type { Candidate } from '@/types/api.ts'
import { getWikipediaSummary, getWikipediaPerson } from '@/services/WikipediaService.ts'

const route = useRoute()
const partyName = ref<string>('')
const routeName = route.params.name as string
const loading = ref(true)
const errorMessage = ref('')
const candidates = ref<Candidate[]>([])
const wikiSummary = ref<string>('')
const wikiUrl = ref<string>('')
const wikiImage = ref<string>('')
const partyLeader = ref<Candidate | null>(null)
const leaderImage = ref<string>('')

onMounted(async () => {
  try {
    const data = await getParties()
    console.log(data)

    const partiesArray = data.parties ?? data
    const foundParty = partiesArray.find((p) => p.name.toLowerCase() === routeName.toLowerCase())
    console.log(foundParty)

    if (foundParty) {
      partyName.value = foundParty.name
      candidates.value = foundParty.candidates ?? []
      const partyleader = candidates.value.find((c) => c.candidateId === '1')
      if (partyleader) {
        partyLeader.value = partyleader
        candidates.value = candidates.value.filter((c) => c.candidateId !== '1')

        const fullName = `${partyleader.firstName} ${partyleader.lastName}`
        try {
          const info = await getWikipediaPerson(fullName)
          leaderImage.value = info.image
        } catch (err) {
          console.warn('Geen Wikipedia-profiel voor lijsttrekker:', err)
        }
      }
      console.log('Kandidaten gevonden:', candidates.value)

      try {
        const summary = await getWikipediaSummary(foundParty.name)
        console.log('Wikipedia parsed summary:', summary)
        wikiSummary.value = (summary.summary || '').replace(/\n/g, '<br><br>')
        wikiUrl.value = summary.url || ''
        wikiImage.value = summary.image || ''
      } catch (wikiErr) {
        console.warn('Wikipedia niet gevonden:', wikiErr)
        wikiSummary.value = 'Geen Wikipedia-informatie beschikbaar voor deze partij.'
      }
    } else {
      errorMessage.value = `De partij "${routeName}" bestaat niet.`
      console.warn('Geen partij gevonden met naam:', routeName)
    }
  } catch (err) {
    console.error('Kon partijen niet ophalen:', err)
    errorMessage.value =
      'Er is een fout opgetreden bij het ophalen van de partijen. Probeer het later opnieuw.'
  } finally {
    loading.value = false
  }
})

const scrollContainer = ref<HTMLElement | null>(null)

const scrollAmount = 150

const scrollLeft = () => {
  if (scrollContainer.value) {
    scrollContainer.value.scrollBy({ left: -scrollAmount, behavior: 'smooth' })
  }
}

const scrollRight = () => {
  if (scrollContainer.value) {
    scrollContainer.value.scrollBy({ left: scrollAmount, behavior: 'smooth' })
  }
}
</script>

<template>
  <div class="min-h-screen text-white">
    <div v-if="loading" class="text-center text-gray-400 text-2xl py-20">
      Even geduld... de partij worden geladen.
    </div>

    <div v-else-if="errorMessage" class="text-center py-20">
      <h2 class="text-4xl font-semibold mb-4">Oeps!</h2>
      <p class="text-gray-300 text-xl">{{ errorMessage }}</p>
    </div>

    <div v-else>
      <header class="flex flex-col md:flex-row justify-between px-8 pt-16 pb-0 gap-8">
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-3">
            <img
              v-if="wikiImage"
              :src="wikiImage"
              :alt="`Logo van ${partyName}`"
              class="max-h-10 md:max-h-18 object-contain rounded-lg"
              style="width: auto"
            />
            <h2 class="text-5xl font-semibold">Partij {{ partyName }}</h2>
          </div>

          <p class="text-gray-300 mt-3 text-xl md:text-2xl">
            Ontdek de standpunten van Partij
            <span class="font-bold text-white"> {{ partyName }}</span>
          </p>

          <div class="relative mt-8 max-w-[650px] w-full">
            <button
              v-if="candidates.length > 5"
              @click="scrollLeft"
              class="absolute left-0 top-1/2 -translate-y-1/2 bg-[#0B132B] hover:bg-[#253054] text-white p-2 rounded-full shadow-md z-10"
            >
              ◀
            </button>

            <div
              ref="scrollContainer"
              v-if="candidates.length"
              class="flex gap-3 overflow-x-auto scrollbar-hide py-3 px-10 rounded-xl"
            >
              <span
                v-for="cand in candidates"
                :key="cand.candidateId"
                class="bg-[#0B132B] px-4 py-2 rounded-full whitespace-nowrap cursor-pointer hover:bg-[#253054] transition text-sm md:text-base"
              >
                {{ cand.firstName }} {{ cand.lastName }}
              </span>
            </div>

            <button
              v-if="candidates.length > 5"
              @click="scrollRight"
              class="absolute right-0 top-1/2 -translate-y-1/2 bg-[#0B132B] hover:bg-[#253054] text-white p-2 rounded-full shadow-md z-10"
            >
              ▶
            </button>
          </div>
        </div>
        <div class="flex-1 flex justify-end items-end">
          <img
            src="@/assets/detail-illustratie.png"
            alt="partij illustratie"
            class="w-80 md:w-96 lg:w-[460px] max-h-[360px] object-contain translate-y-[19px]"
          />
        </div>
      </header>

      <section class="bg-[#0B132B] px-0 lg:px-12 py-12">
        <div
          class="max-w-[1600px] mx-auto grid grid-cols-1 lg:grid-cols-[1.8fr_0.8fr] gap-28 items-start"
        >
          <div class="pl-4 lg:pl-0">
            <h3 class="text-4xl font-semibold mb-4">Over {{ partyName }}</h3>
            <p class="text-gray-300" v-html="wikiSummary"></p>
            <div v-if="wikiUrl" class="mt-4">
              <a :href="wikiUrl" target="_blank" class="text-[#EF3054] underline">
                Lees verder op Wikipedia →
              </a>
            </div>
          </div>

          <div
            v-if="partyLeader"
            class="bg-[#040C25] border border-gray-700/30 rounded-2xl p-5 flex gap-4 items-center justify-center"
          >
            <img
              v-if="leaderImage"
              :src="leaderImage"
              :alt="`Foto van ${partyLeader.firstName} ${partyLeader.lastName}`"
              class="max-h-40 object-contain rounded-xl shadow-lg"
              style="width: auto"
            />
            <div>
              <p class="text-sm text-gray-400 uppercase tracking-wide mb-1">Partijleider</p>
              <h4 class="text-lg font-semibold">
                {{ partyLeader.firstName }} {{ partyLeader.lastName }}
              </h4>
              <p class="text-gray-400">
                {{ partyLeader.localityName }}
              </p>
            </div>
          </div>
        </div>
      </section>

      <!--      <section class="px-8 mt-4">-->
      <!--        <h3 class="text-3xl font-semibold mb-4 text-center">Kandidaten</h3>-->

      <!--        <div class="relative max-w-[1200px] mx-auto">-->
      <!--          <button-->
      <!--            v-if="candidates.length > 5"-->
      <!--            @click="scrollLeft"-->
      <!--            class="absolute left&#45;&#45;6 top-1/2 -translate-y-1/2 bg-[#0B132B] hover:bg-[#253054] text-white p-2 rounded-full shadow-md z-10"-->
      <!--          >-->
      <!--            ◀-->
      <!--          </button>-->

      <!--          <div-->
      <!--            ref="scrollContainer"-->
      <!--            v-if="candidates.length"-->
      <!--            class="flex gap-3 overflow-x-auto scrollbar-hide py-3 px-10"-->
      <!--          >-->
      <!--            <span-->
      <!--              v-for="cand in candidates"-->
      <!--              :key="cand.candidateId"-->
      <!--              class="bg-[#0B132B] px-4 py-2 rounded-full whitespace-nowrap cursor-pointer hover:bg-[#253054] transition text-sm md:text-base"-->
      <!--            >-->
      <!--              {{ cand.firstName }} {{ cand.lastName }}-->
      <!--            </span>-->
      <!--          </div>-->

      <!--          <button-->
      <!--            v-if="candidates.length > 5"-->
      <!--            @click="scrollRight"-->
      <!--            class="absolute right-0 top-1/2 -translate-y-1/2 bg-[#0B132B] hover:bg-[#253054] text-white p-2 rounded-full shadow-md z-10"-->
      <!--          >-->
      <!--            ▶-->
      <!--          </button>-->
      <!--        </div>-->
      <!--      </section>-->

      <section class="px-8 py-12 mt-8">
        <div class="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-[1.2fr_1.8fr] gap-6">
          <div class="flex flex-col gap-6">
            <div
              class="bg-[#0B132B] p-6 rounded-2xl shadow-md text-gray-200 border border-gray-700/30"
            >
              <h4 class="text-xl font-semibold mb-2">Standpunt 1</h4>
              <p class="text-gray-400 leading-relaxed">
                Lorem ipsum dolor sit amet. Vel dignissimos nihil sit dignissimos.
              </p>
            </div>

            <div
              class="bg-[#0B132B] p-6 rounded-2xl shadow-md text-gray-200 border border-gray-700/30"
            >
              <h4 class="text-xl font-semibold mb-2">Standpunt 2</h4>
              <p class="text-gray-400 leading-relaxed">
                Quo ipsa provident sit autem soluta et earum distinctio. Aut exercitationem dolores
                ab officia commodi et consequuntur animi.
              </p>
            </div>
          </div>

          <div
            class="bg-[#0B132B] p-6 rounded-2xl shadow-md text-gray-200 border border-gray-700/30 flex flex-col justify-center"
          >
            <h4 class="text-xl font-semibold mb-2 flex items-center gap-2">
              <span class="text-orange-400 text-2xl">⭐</span> Standpunt 3
            </h4>
            <p class="text-gray-400 leading-relaxed">
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam eget odio quis lacus
              volutpat feugiat quis elementum nunc. Pellentesque rhoncus ex justo. Integer interdum
              ullamcorper viverra. Ut ac felis tempor nisi aliquet suscipit. Praesent eu libero
              justo. Duis scelerisque egestas condimentum. Mauris elementum tellus eget turpis
              iaculis, vel ornare orci congue. Proin vel iaculis sapien, vel rhoncus risus. Integer
              justo neque, blandit ut leo nec, euismod posuere nulla.
            </p>
          </div>
        </div>

        <div class="flex justify-center mt-8 gap-2 text-gray-400 items-center">
          <button class="px-3 py-1 rounded-lg border border-gray-700 hover:border-gray-500">
            ← Previous
          </button>

          <span class="px-3 py-1 bg-[#EF3054] text-white rounded-lg">1</span>

          <button class="px-3 py-1 rounded-lg border border-gray-700 hover:border-gray-500">
            Next →
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
