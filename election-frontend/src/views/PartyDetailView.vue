<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getPartyDetail } from '@/services/PartyService'
import type { Candidate, PartyDetail } from '@/types/api'
import { getWikipediaSummary, getWikipediaPerson } from '@/services/WikipediaService'

const route = useRoute()

const partyId = computed(() => route.params.partyId as string)

const partyName = ref<string>('')
const loading = ref(true)
const errorMessage = ref('')
const candidates = ref<Candidate[]>([])
const wikiSummary = ref<string>('')
const wikiUrl = ref<string>('')
const wikiImage = ref<string>('')
const partyLeader = ref<Candidate | null>(null)
const leaderImage = ref<string>('')

const scrollContainer = ref<HTMLElement | null>(null)
const scrollAmount = 150

onMounted(async () => {
  loading.value = true
  errorMessage.value = ''

  if (!partyId.value) {
    errorMessage.value = 'Geen partij-id gevonden in de URL.'
    loading.value = false
    return
  }

  try {
    const detail: PartyDetail = await getPartyDetail(partyId.value)

    partyName.value = detail.name
    candidates.value = detail.candidates ?? []

    const leader = candidates.value.find((c) => c.candidateId === '1')
    if (leader) {
      partyLeader.value = leader
      candidates.value = candidates.value.filter((c) => c.candidateId !== '1')

      const fullName = leader.namePrefix
        ? `${leader.firstName} ${leader.namePrefix} ${leader.lastName}`
        : `${leader.firstName} ${leader.lastName}`

      try {
        const info = await getWikipediaPerson(fullName)
        leaderImage.value = info.image
      } catch (err) {
        console.warn('Geen Wikipedia-profiel voor lijsttrekker:', err)
      }
    }

    try {
      const summary = await getWikipediaSummary(detail.name)
      wikiSummary.value = (summary.summary || '').replace(/\n/g, '<br><br>')
      wikiUrl.value = summary.url || ''
      wikiImage.value = summary.image || ''
    } catch (wikiErr) {
      console.warn('Wikipedia niet gevonden:', wikiErr)
      wikiSummary.value = 'Geen Wikipedia-informatie beschikbaar voor deze partij.'
    }
  } catch (err) {
    console.error('Kon partijdetail niet ophalen:', err)
    errorMessage.value =
      'Er is een fout opgetreden bij het ophalen van de partij. Probeer het later opnieuw.'
  } finally {
    loading.value = false
  }
})

const scrollLeft = () => {
  scrollContainer.value?.scrollBy({ left: -scrollAmount, behavior: 'smooth' })
}

const scrollRight = () => {
  scrollContainer.value?.scrollBy({ left: scrollAmount, behavior: 'smooth' })
}
</script>

<template>
  <div class="min-h-screen text-white">
    <div v-if="loading" class="text-center text-gray-400 text-2xl py-20">
      Even geduld... de partij wordt geladen.
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

          <div class="relative mt-8 flex items-center w-full">
            <button
              v-if="candidates.length > 5"
              @click="scrollLeft"
              class="bg-[#0B132B] hover:bg-[#253054] text-white p-2 rounded-full shadow-md mx-2"
            >
              ◀
            </button>

            <div
              ref="scrollContainer"
              v-if="candidates.length"
              class="flex gap-3 overflow-x-auto scrollbar-hide py-3 px-2 rounded-xl max-w-[580px] w-full justify-start"
            >
              <a
                v-for="cand in candidates"
                :key="cand.candidateId"
                class="bg-[#0B132B] px-4 py-2 rounded-full whitespace-nowrap hover:bg-[#253054] transition text-sm md:text-base"
              >
                {{ cand.firstName }}
                <span v-if="cand.namePrefix"> {{ cand.namePrefix }}</span>
                {{ cand.lastName }}
              </a>
            </div>

            <button
              v-if="candidates.length > 5"
              @click="scrollRight"
              class="bg-[#0B132B] hover:bg-[#253054] text-white p-2 rounded-full shadow-md mx-2"
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
                {{ partyLeader.firstName }}
                <span v-if="partyLeader.namePrefix"> {{ partyLeader.namePrefix }}</span>
                {{ partyLeader.lastName }}
              </h4>
              <p class="text-gray-400">
                {{ partyLeader.localityName }}
              </p>
            </div>
          </div>
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
