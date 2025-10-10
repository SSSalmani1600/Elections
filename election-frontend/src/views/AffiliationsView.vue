<script setup lang="ts">

import {onMounted, ref} from "vue";
import type {AffiliationResponse, Affiliation} from "@/types/api.ts";
import {getAffiliations} from "@/services/AffiliationService.ts";
import IconSpinner from "@/components/icons/IconSpinner.vue";

const data = ref<string[]>([]);
const loading = ref(false);

onMounted(async () => {
  loading.value = true;
  try {
    data.value = await getAffiliations();
    console.log(data.value);
  } catch (err: any) {
    console.log(err.message)
  } finally {
    loading.value = false;
  }
})

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
      <div>
        <div class="grid grid-cols-3 gap-x-6 gap-y-4">
          <a v-for="affiliation in data" :key="affiliation" href="/"
             class="flex gap-8 bg-background! h-[150px] border rounded-lg border-[#455174]! overflow-hidden p-4!">
            <img src="../assets/partij-img.svg" width="120px" alt="" class="mb-auto">
            <div class="overflow-hidden">
              <div class="flex flex-col max-w-[250px]">
                <span class="text-lg font-bold truncate">{{ affiliation }}</span>
                <p class="text-text-muted  line-clamp-3"> simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, </p>
              </div>
            </div>
          </a>
        </div>
        <IconSpinner v-if="loading"/>
      </div>
    </div>

  </div>
</template>

<style scoped>
</style>
