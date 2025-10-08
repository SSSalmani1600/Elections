<script setup lang="ts">

import {onMounted, ref} from "vue";
import type {AffiliationResponse, Affiliation} from "@/types/api.ts";
import {getAffiliations} from "@/services/AffiliationService.ts";
import IconSpinner from "@/components/icons/IconSpinner.vue";

const data = ref<AffiliationResponse | null>(null);
const affiliations = ref<Affiliation[]>([]);
const loading = ref(false);

onMounted(async () => {
  loading.value = true;
  try {
    data.value = await getAffiliations();
    affiliations.value = data.value.affiliations;
  } catch (err: any) {
    console.log(err.message)
  } finally {
    loading.value = false;
  }
})

</script>

<template>
  <div class="w-full flex flex-col items-center">
    <div class="flex flex-col items-center w-[70%] gap-12">
      <div class="flex justify-between items-baseline w-full">
        <div>
          <h1 class="text-[3rem] font-bold">Partijen</h1>
          <p class="text-text-muted">Hier vind je alle partijen die er op dit moment bestaan</p>
        </div>
        <div>
          <input type="text" placeholder="Zoek een partij">
        </div>
      </div>
      <div>
        <div>

          <div v-for="affiliation in affiliations">
            <h2>{{ affiliation.name }}</h2>
          </div>
        </div>
        <IconSpinner v-if="loading"/>
      </div>
    </div>

  </div>
</template>

<style scoped>
</style>
