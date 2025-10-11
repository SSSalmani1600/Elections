<script setup lang="ts">

import {computed, onMounted, ref} from "vue";
import {getParties} from "@/services/PartyService.ts";
import IconSpinner from "@/components/icons/IconSpinner.vue";

const data = ref<string[]>([]);
const loading = ref(false);

onMounted(async () => {
  loading.value = true;
  updatePageSize();
  try {
    data.value = await getParties();
  } catch (err: any) {
    console.log(err.message)
  } finally {
    loading.value = false;
  }
})


const pageSize = ref(9);
const currentPage = ref(1);

const totalPages = computed(() => Math.ceil(data.value.length / pageSize.value));
const maxVisiblePages = ref(3);

const pages = computed(() => {
  const total = totalPages.value;
  const current = currentPage.value;
  const result: (number | string)[] = [];

  if (total <= maxVisiblePages.value) {
    for (let i = 1; i <= total; i++) result.push(i);
    return result;
  }

  const half = Math.floor(maxVisiblePages.value / 2);
  let start = Math.max(2, current - half);
  let end = Math.min(total - 1, current + half);

  if (current <= half) {
    start = 2;
    end = maxVisiblePages.value - 1;
  }
  if (current >= total - half) {
    start = total - (maxVisiblePages.value - 2);
    end = total - 1;
  }

  result.push(1);

  if (start > 2) result.push('...');

  for (let i = start; i <= end; i++) result.push(i);

  if (end < total - 1) result.push('...');

  result.push(total);

  return result;
});

const visibleParties = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = currentPage.value * pageSize.value;
  return data.value.slice(start, end);
});

const selectPage = (page: number | string) => {
  if (typeof page === "number")
  currentPage.value = page;
}

function updatePageSize() {
  const width = window.innerWidth;

  if (width >= 1280) {
    pageSize.value = 9;
    maxVisiblePages.value = 3;
  } else if (width >= 768) {
    pageSize.value = 6;
    maxVisiblePages.value = 3;
  } else {
    pageSize.value = 3;
    maxVisiblePages.value = 1;
  }
}

window.addEventListener("resize", updatePageSize);

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
      <div class="flex flex-col items-center gap-6 min-h-[542px] relative">
        <div class="grid grid-cols-3 gap-x-6 gap-y-4 max-md:grid-cols-1 max-xl:grid-cols-2">
          <div v-for="party in visibleParties" :key="party"
               class="bg-primary w-full h-fit rounded-lg">
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
        <div class="flex items-center gap-2 mt-auto">
          <button class="pagination-btn" @click="currentPage--" :disabled="(currentPage === 1)"><i
            class="pi pi-arrow-left"></i> <span class="hidden md:block">Vorige</span>
          </button>
          <div class="flex items-center gap-1">
            <button v-for="page in pages" :key="page" :class="{'active-page' :page === currentPage}"
                    @click="selectPage(page)" class="py-1.5 px-3.5 rounded-lg cursor-pointer">{{
                page
              }}
            </button>
          </div>
          <button class="pagination-btn" @click="currentPage++"
                  :disabled="(currentPage === totalPages)"><span
            class="hidden md:block">Volgende</span> <i
            class="pi pi-arrow-right"></i></button>
        </div>
        <IconSpinner v-if="loading" class="absolute top-1/2 left-1/2 transform -translate-1/2"/>
      </div>
    </div>

  </div>
</template>

<style scoped>
.pagination-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  transition-duration: 200ms;
}

.pagination-btn:active {
  color: #EF3054;
}

.pagination-btn[disabled] {
  cursor: default;
  opacity: 60%;
}

.pagination-btn[disabled]:active {
  color: white;
}

.active-page {
  background: #EF3054;
  transition-duration: 500ms;
}
</style>
