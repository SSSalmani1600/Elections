<script setup lang="ts">

import {computed, onMounted, ref} from "vue";
import {getParties} from "@/services/PartyService.ts";
import IconSpinner from "@/components/icons/IconSpinner.vue";

const data = ref<string[]>([]);
const loading = ref(false);

onMounted(async () => {
  loading.value = true;
  try {
    data.value = await getParties();
  } catch (err: any) {
    console.log(err.message)
  } finally {
    loading.value = false;
  }
})


const pageSize = 9;
const currentPage = ref(1);

const totalPages = computed(() => Math.ceil(data.value.length / pageSize));
const pages = computed((): number[] =>
  Array.from({ length: totalPages.value }, (_, i) => i + 1)
);

const visibleParties = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = currentPage.value * pageSize;
  return data.value.slice(start, end);
});

const selectPage = (page: number) => {
  currentPage.value = page;
}

</script>

<template>
  <div class="w-full flex flex-col items-center">
    <div class="flex flex-col items-center w-[70%] gap-10">
      <div class="flex justify-between items-baseline w-full">
        <div>
          <h1 class="text-[3rem] font-bold">Partijen</h1>
          <p class="text-text-muted">Hier vind je alle partijen die er op dit moment bestaan</p>
        </div>
      </div>
      <div class="flex flex-col items-center gap-6">
        <div class="grid grid-cols-3 gap-x-6 gap-y-4">
          <div v-for="party in visibleParties" :key="party"
               class="bg-primary w-full h-full rounded-lg">
            <a href="/"
               class="flex gap-8 bg-background! h-[150px] border rounded-lg border-[#455174]! overflow-hidden p-4! ease-out hover:transform hover:-translate-x-2 hover:-translate-y-2 hover:shadow-lg duration-300">
              <img src="../assets/partij-img.svg" width="120px" alt="" class="mb-auto">
              <div class="overflow-hidden">
                <div class="flex flex-col max-w-[250px]">
                  <span class="text-lg font-bold truncate">{{ party }}</span>
                  <p class="text-text-muted  line-clamp-3"> simply dummy text of the printing and
                    typesetting industry. Lorem Ipsum has been the industry's standard dummy text
                    ever since the 1500s, </p>
                </div>
              </div>
            </a>
          </div>
        </div>
        <div class="flex items-center gap-4">
          <button class="pagination-btn" @click="currentPage--" :disabled="(currentPage === 1)"><i
            class="pi pi-arrow-left"></i> Vorige
          </button>
          <button v-for="page in pages" :key="page" :class="{'active-page' :page === currentPage}" @click="selectPage(page)" class="py-1.5 px-3 rounded-lg cursor-pointer">{{ page }}</button>
          <button class="pagination-btn" @click="currentPage++"
                  :disabled="(currentPage === totalPages)">Volgende <i
            class="pi pi-arrow-right"></i></button>
        </div>
        <IconSpinner v-if="loading"/>
      </div>
    </div>

  </div>
</template>

<style scoped>
.pagination-btn {
  cursor: pointer;
}

.pagination-btn[disabled] {
  cursor: default;
  opacity: 60%;
}

.active-page {
  background: #EF3054;
  transition-duration: 500ms;
}
</style>
