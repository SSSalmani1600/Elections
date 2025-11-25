<script setup lang="ts">

import {onMounted, ref} from "vue";
import type {Statement} from "@/types/api.ts";
import {getAllStatements} from "@/services/StatementService.ts";

const data = ref<Statement[]>([]);
const loading = ref<boolean>(false);

onMounted(async () => {
  try {
    loading.value = true;
    data.value = Array.from(await getAllStatements())
    console.log(data.value)
  } catch (err: any) {
    console.error(err.message);
  } finally {
    loading.value = false;
  }
})
</script>

<template>
  <div>
    <span>VRAGEN</span>
    <div>
      <div v-for="(statement, index) in data">
        <span>{{ index + 1 }} - {{ statement.statement }}</span>
        <span>{{ statement.category}}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
