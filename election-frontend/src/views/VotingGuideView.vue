<script setup lang="ts">

import {onMounted, ref} from "vue";
import type {Statement} from "@/types/api.ts";
import {getAllStatements} from "@/services/StatementService.ts";

const data = ref<Statement[]>([]);
const loading = ref<boolean>(false);
const selectedStatement = ref<Statement>();

const selectStatement = (statement: Statement) => {
  selectedStatement.value = statement;
}
onMounted(async () => {
  try {
    loading.value = true;
    data.value = Array.from(await getAllStatements())
    console.log(data.value)

    const firstUnanswered = data.value.find(statement => {
      return !localStorage.getItem(`answer_${statement.id}`);
    });

    if (firstUnanswered) {
      selectedStatement.value = firstUnanswered;
    } else if (data.value.length > 0) {
      selectedStatement.value = data.value[0];
    }
  } catch (err: any) {
    console.error(err.message);
  } finally {
    loading.value = false;
  }
})
</script>

<template>
  <div class="flex flex-col gap-4 m-[54px]">
    <span class="font-bold text-[28px]">VRAGEN</span>
    <div class="grid grid-cols-12">

      <div class="col-span-3 bg-background rounded-[10px] max-h-[575px] overflow-x-hidden">
        <button v-if="loading" v-for="i in 10" class="flex flex-col gap-2.5 p-4 items-start w-full">
          <span class="skeleton-text h-4 w-full rounded-[10px]"></span>
          <span class="skeleton-text h-4 w-[40%] rounded-[10px]"></span>
        </button>

        <button v-if="!loading" v-for="(statement, index) in data" :key="statement.id" @click="selectStatement(statement)" :class="selectedStatement?.id === statement.id ? 'bg-primary' : ''" class="flex flex-col cursor-pointer gap-1 p-4 items-start hover:bg-primary duration-300">
          <span class="font-bold text-2xl truncate">{{ index + 1 }} - {{ statement.statement }}</span>
          <span class="opacity-80">{{ statement.category }}</span>
        </button>
      </div>
      <div>
      </div>
    </div>
  </div>
</template>

<style scoped>
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
