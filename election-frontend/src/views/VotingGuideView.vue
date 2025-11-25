<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { Statement } from '@/types/api.ts'
import { getAllStatements } from '@/services/StatementService.ts'

const data = ref<Statement[]>([])
const loading = ref<boolean>(false)
const selectedStatement = ref<Statement | null>(null)

const selectStatement = (statement: Statement) => {
  selectedStatement.value = statement
}

const saveAnswer = (statementId: number, answer: string) => {
  const stored = JSON.parse(localStorage.getItem('voting_guide_answers') || '{}')
  stored[(statementId)] = answer
  localStorage.setItem('voting_guide_answers', JSON.stringify(stored))

  findUnansweredQuestion()
}

const findUnansweredQuestion = () => {
  const storedAnswers = JSON.parse(localStorage.getItem('voting_guide_answers') || '{}')

  const firstUnanswered = data.value.find(statement => !storedAnswers[statement.id])

  if (firstUnanswered) {
    selectedStatement.value = firstUnanswered
  } else if (data.value.length > 0) {
    selectedStatement.value = data.value[0]
  }
}

onMounted(async () => {
  try {
    loading.value = true
    data.value = Array.from(await getAllStatements())

    if (!localStorage.getItem('voting_guide_answers')) {
      localStorage.setItem('voting_guide_answers', '{}')
    }

    findUnansweredQuestion()
  } catch (err: any) {
    console.error(err.message)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="flex flex-col gap-4 m-[54px]">
    <span class="font-bold text-[28px]">STELINGEN</span>
    <div class="grid grid-cols-12 gap-20">
      <div class="col-span-3 bg-background rounded-[10px] max-h-[575px] overflow-y-scroll overflow-x-hidden">
        <template v-if="loading">
          <button v-for="i in 10" :key="i" class="flex flex-col gap-2.5 p-4 items-start w-full">
            <span class="skeleton-text h-4 w-full rounded-[10px]"></span>
            <span class="skeleton-text h-4 w-[40%] rounded-[10px]"></span>
          </button>
        </template>

        <template v-else>
          <button
            v-for="(statement, index) in data"
            :key="statement.id"
            @click="selectStatement(statement)"
            :class="selectedStatement?.id === statement.id ? 'bg-primary' : ''"
            class="flex flex-col cursor-pointer gap-1 w-full p-4 items-start hover:bg-primary duration-300"
          >
            <span class="font-bold block w-full text-lg truncate"
              >{{ index + 1 }} - {{ statement.statement }}</span
            >
            <span class="opacity-80">{{ statement.category }}</span>
          </button>
        </template>
      </div>
      <div class="col-span-7 flex flex-col gap-10">
        <div class="flex flex-col gap-6">
          <div class="w-full flex justify-between items-center pb-6 border-b-2 border-white">
            <span class="px-2.5 py-2 rounded-[10px] font-bold bg-primary">{{
              selectedStatement?.category
            }}</span>
            <span class="font-bold text-3xl">STELLING - {{ selectedStatement?.id }}</span>
          </div>
          <div class="flex flex-col gap-4 bg-background p-4 rounded-lg">
            <p class="text-[28px] font-bold">{{ selectedStatement?.statement }}</p>
            <p class="opacity-80 text-lg">{{ selectedStatement?.explanation }}</p>
          </div>
        </div>
        <div class="flex flex-col gap-4">
          <span class="font-bold text-[28px]">Hiermee ben ik het</span>
          <div class="flex items-center gap-3 text-lg">
            <button @click="saveAnswer(selectedStatement!.id, 'EENS')" class="btn opinion-btn !border-[#48D507] hover:bg-[#48D507]">EENS</button>
            <span class="text-2xl">/</span>
            <button @click="saveAnswer(selectedStatement!.id, 'NEUTRAAL')" class="btn opinion-btn hover:bg-white hover:text-black">NEUTRAAL</button>
            <span class="text-2xl">/</span>
            <button @click="saveAnswer(selectedStatement!.id, 'ONEENS')" class="btn opinion-btn !border-[#FF1E00] hover:bg-[#FF1E00]">ONEENS</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.opinion-btn {
  padding: 4px 10px !important;
  font-weight: 600;
  border: 1px solid;
  transition-duration: 400ms;
  border-radius: 10px;
}

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
