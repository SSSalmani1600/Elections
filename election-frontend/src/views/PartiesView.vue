<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { getParties } from '@/services/PartyService.ts'
import { Search } from 'lucide-vue-next'
import { Input } from '@/components/ui/input'
import type { Party2 } from '@/types/api.ts'
import { getWikipediaPartyData } from '@/services/WikipediaService.ts'
import { useFuse } from '@vueuse/integrations/useFuse'

const data = ref<Party2[]>([])
const partyWithInfo = ref<{ name: string; img: string; summary: string }[]>([])
const loading = ref(false)
const hasError = ref<boolean>(false)
const inputText = ref<string>('')
const { results } = useFuse(inputText, partyWithInfo, {
  fuseOptions: {
    keys: ['name'],
    threshold: 0.3,
  },
})

onMounted(async () => {
  loading.value = true
  updatePageSize()
  try {
    data.value = Array.from(await getParties())
    const promises = data.value.map(async (party) => {
      const wikiInfo = await getWikipediaPartyData(party.name)
      return {
        name: party.name,
        img: wikiInfo?.img || '',
        summary: wikiInfo?.summary || '',
      }
    })
    partyWithInfo.value = await Promise.all(promises)
  } catch (err: any) {
    console.error(err.message)
    hasError.value = true
  } finally {
    loading.value = false
    console.log(loading.value)
  }
})

const pageSize = ref(9)
const currentPage = ref(1)

const totalPages = computed(() => Math.ceil(filteredList.value.length / pageSize.value))
const maxVisiblePages = ref(3)

const pages = computed(() => {
  const current = currentPage.value
  const result: (number | string)[] = []
  const total = totalPages.value
  if (total <= maxVisiblePages.value) {
    for (let i = 1; i <= total; i++) result.push(i)
    return result
  }

  const half = Math.floor(maxVisiblePages.value / 2)
  let start = Math.max(2, current - half)
  let end = Math.min(total - 1, current + half)

  if (current <= half) {
    start = 2
    end = maxVisiblePages.value - 1
  }
  if (current >= total - half) {
    start = total - (maxVisiblePages.value - 2)
    end = total - 1
  }

  result.push(1)

  if (start > 2) result.push('...')

  for (let i = start; i <= end; i++) result.push(i)

  if (end < total - 1) result.push('...')

  result.push(total)

  return result
})

const filteredList = computed(() => {
  currentPage.value = 1
  return inputText.value.trim() === '' ? partyWithInfo.value : results.value.map((r) => r.item)
})

const visibleParties = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = currentPage.value * pageSize.value
  return filteredList.value.slice(start, end)
})

const selectPage = (page: number | string) => {
  if (typeof page === 'number') currentPage.value = page
}

function updatePageSize() {
  const width = window.innerWidth

  if (width >= 1280) {
    pageSize.value = 9
    maxVisiblePages.value = 3
    currentPage.value = 1
  } else if (width >= 768) {
    pageSize.value = 6
    maxVisiblePages.value = 3
    currentPage.value = 1
  } else {
    pageSize.value = 3
    maxVisiblePages.value = 1
    currentPage.value = 1
  }
}

window.addEventListener('resize', updatePageSize)

watch([inputText, filteredList, loading], ([newInput, newList]) => {
  hasError.value = !loading.value && newInput.trim() !== '' && newList.length === 0
})
</script>

<template>
  <div class="w-full flex flex-col items-center">
    <div class="flex flex-col items-center w-[70%] gap-10">
      <div class="flex flex-col justify-between w-full mt-8 gap-4 xl:items-end xl:flex-row">
        <div>
          <h1 class="text-[3rem] font-bold">Partijen</h1>
          <p class="text-text-muted max-w-[600px]">
            Op deze pagina vind je een overzicht van alle bestaande partijen. Lees meer over hun
            visie en beleid door op 1 van de partijen de klikken
          </p>
        </div>
        <div class="relative flex w-full max-w-sm items-center">
          <span class="absolute end-0 inset-y-0 flex items-center justify-center px-2">
            <Search class="size-6 text-primary" />
          </span>
          <Input
            v-model="inputText"
            id="search"
            onchange=""
            type="text"
            placeholder="Search..."
            class="pr-10 rounded-2xl bg-background border-none active:outline-none focus-visible:ring-1"
          />
        </div>
      </div>
      <div class="flex flex-col items-center gap-6 max-h-[542px] w-full">
        <div class="relative w-full min-h-[482px]">
          <span
            v-show="hasError"
            class="text-lg bg-background py-4 px-8 rounded-lg shadow-lg text-primary absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-center w-full md:w-fit"
            >Er konden geen partijen gevonden worden!</span
          >
          <div
            v-show="!hasError && visibleParties.length > 0 && !loading"
            class="grid grid-cols-3 gap-x-6 gap-y-4 max-md:grid-cols-1 max-xl:grid-cols-2"
          >
            <router-link
              :to="{ path: '/partij/' + party.name }"
              v-for="party in visibleParties"
              :key="party.name"
              class="bg-primary w-full h-fit rounded-lg"
            >
              <div
                class="flex gap-8 bg-background  h-[150px] border rounded-lg border-[#455174]! overflow-hidden p-4! ease-out hover:transform hover:-translate-x-2 hover:-translate-y-2 hover:shadow-lg duration-300"
              >
                <span class="w-[120px] h-full">
                  <img :src="party.img" alt="" class="w-full h-full object-contain " />
                </span>
                <div class="overflow-hidden">
                  <div class="flex flex-col max-w-[250px]">
                    <span class="text-lg  font-bold truncate">{{ party.name }}</span>
                    <p class="text-text-muted line-clamp-3">{{ party.summary }}</p>
                  </div>
                </div>
              </div>
            </router-link>
          </div>

          <div
            v-if="loading"
            class="grid grid-cols-3 gap-x-6 gap-y-4 max-md:grid-cols-1 max-xl:grid-cols-2"
          >
            <div
              v-for="n in pageSize"
              :key="n"
              class="bg-background border rounded-lg border-[#455174] h-[150px] p-4 flex gap-8"
            >
              <div class="skeleton-img w-[120px] h-full rounded-lg"></div>

              <div class="flex flex-col gap-3 w-full">
                <div class="skeleton-text h-5 w-[70%] rounded-lg"></div>
                <div class="skeleton-text h-4 w-full rounded-lg"></div>
                <div class="skeleton-text h-4 w-[85%] rounded-lg"></div>
              </div>
            </div>
          </div>
        </div>

        <div
          v-show="!hasError && visibleParties.length > 0"
          class="flex items-center justify-between w-[252px] gap-2 mt-auto md:w-[400px]"
        >
          <button class="pagination-btn" @click="currentPage--" :disabled="currentPage === 1">
            <i class="pi pi-arrow-left"></i>
            <span class="hidden md:block">Vorige</span>
          </button>
          <div class="flex items-center gap-1">
            <button
              v-for="page in pages"
              :key="page"
              :class="{ 'active-page': page === currentPage }"
              @click="selectPage(page)"
              class="page-option"
            >
              {{ page }}
            </button>
          </div>
          <button
            class="pagination-btn"
            @click="currentPage++"
            :disabled="currentPage === totalPages"
          >
            <span class="hidden md:block">Volgende</span>
            <i class="pi pi-arrow-right"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.skeleton-img,
.skeleton-text {
  background: linear-gradient(90deg, #3e3e3e 0%, #555 50%, #3e3e3e 100%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.2s infinite;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

</style>
