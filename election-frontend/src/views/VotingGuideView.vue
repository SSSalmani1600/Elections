<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue'
import type { Statement } from '@/types/api.ts'
import { getAllStatements } from '@/services/StatementService.ts'
import ProgressBar from '@/components/ProgressBar.vue'

const data = ref<Statement[]>([])
const loading = ref<boolean>(false)
const selectedStatement = ref<Statement | null>(null)
const errorMessage = ref<string | null>(null)
const statementRefs = ref<Record<number, HTMLButtonElement | null>>({})
const totalStatements = ref<number>(0)
const completedStatements = ref<number>(0)

const selectStatement = (statement: Statement) => {
  selectedStatement.value = statement
}

const saveAnswer = (statementId: number, answer: string) => {
  const stored = JSON.parse(localStorage.getItem('voting_guide_answers') || '{}')
  stored[statementId] = answer
  localStorage.setItem('voting_guide_answers', JSON.stringify(stored))

  const index = data.value.findIndex((item) => item.id === statementId)

  if (index !== -1) {
    data.value[index].answer = answer
  }

  updateAnsweredStatements(stored)

  findUnansweredQuestion()
}

const updateAnsweredStatements = (storedAnswers: object) => {
  completedStatements.value = Object.entries(storedAnswers).length
}

const findUnansweredQuestion = () => {
  const storedAnswers = JSON.parse(localStorage.getItem('voting_guide_answers') || '{}')

  const firstUnanswered = data.value.find((statement) => !storedAnswers[statement.id])

  if (firstUnanswered) {
    selectedStatement.value = firstUnanswered
  } else if (data.value.length > 0) {
    selectedStatement.value = data.value[0]
  }
}

const setStatementRef = (el: HTMLButtonElement | null, index: number) => {
  if (el) {
    statementRefs.value[index] = el
  }
}

watch(selectedStatement, async (newVal) => {
  if (!newVal) return

  await nextTick()

  const index = data.value.findIndex((s) => s.id === newVal.id)
  const el = statementRefs.value[index]

  if (el) {
    el.scrollIntoView({
      behavior: 'smooth',
      block: 'center',
    })
  }
})

onMounted(async () => {
  try {
    loading.value = true
    data.value = Array.from(await getAllStatements())
    const storedAnswers = JSON.parse(localStorage.getItem('voting_guide_answers') || '{}')
    updateAnsweredStatements(storedAnswers)
    data.value = data.value.map((statement) => ({
      ...statement,
      answer: storedAnswers[statement.id] ?? null,
    }))

    totalStatements.value = data.value.length

    findUnansweredQuestion()
  } catch (err: any) {
    errorMessage.value = 'Er ging iets mis bij het ophalen van de statements'
    console.error(err.message)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div
    v-if="errorMessage"
    class="bg-background p-10 rounded-[10px] absolute transform translate-[-50%] top-1/2 left-1/2"
  >
    <span class="text-red-400 font-bold text-xl">ERROR</span>
    <p class="text-lg">{{ errorMessage }}</p>
  </div>
  <div v-else class="m-6 lg:m-[54px]">
    <div class="flex flex-col lg:grid lg:grid-cols-12 lg:items-center gap-20">
      <div class="flex flex-col gap-4 order-2 lg:order-1 lg:col-span-4 2xl:col-span-3">
        <span class="font-bold text-[28px]">STELLINGEN</span>
        <div
          class="bg-background rounded-[10px] max-h-[400px] lg:max-h-[575px] overflow-x-scroll lg:overflow-y-scroll lg:overflow-x-hidden"
        >
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
              :ref="(el) => setStatementRef(el as HTMLButtonElement | null, index)"
              :class="selectedStatement?.id === statement.id ? 'bg-primary' : ''"
              class="flex flex-col cursor-pointer gap-1 w-full p-4 items-start hover:bg-primary duration-300"
            >
              <span class="font-bold block w-full text-lg truncate text-left"
                >{{ index + 1 }} - {{ statement.statement }}</span
              >
              <div class="block truncate w-full text-left">
                <span class="opacity-80">{{ statement.category }}</span>
                <span v-if="statement.answer" class="font-bold">
                  - {{ statement.answer }}
                  <i
                    class="pi pi-check text-[#277D00] font-bold ml-2"
                    style="font-size: 1rem; font-weight: 700"
                  ></i
                ></span>
              </div>
            </button>
          </template>
        </div>
      </div>

      <div
        v-if="loading"
        class="order-1 lg:order-2 lg:col-span-8 2xl:col-span-7 flex flex-col gap-10"
      >
        <div class="flex flex-col gap-6">
          <div class="w-full flex justify-between items-center pb-6 border-b-2 border-white">
            <span class="skeleton-text h-4 w-[10%] rounded-[10px]"></span>
            <span class="skeleton-text h-4 w-[30%] rounded-[10px]"></span>
          </div>
          <div class="flex flex-col gap-4 bg-background p-4 rounded-lg">
            <span class="skeleton-text h-8 w-full rounded-[10px]"></span>
            <span class="skeleton-text h-4 w-full rounded-[10px]"></span>
            <span class="skeleton-text h-4 w-[30%] rounded-[10px]"></span>
          </div>
        </div>
      </div>

      <div v-else class="order-1 lg:order-2 lg:col-span-8 2xl:col-span-7 flex flex-col gap-10">
        <div>
          <div class="w-full flex justify-between">
            <span>Jouw progressie</span>
            <span>{{ completedStatements }} / {{ totalStatements }} stellingen</span>
          </div>
          <ProgressBar :totalAnswered="completedStatements" :totalStatements="totalStatements" class="mt-1"></ProgressBar>
        </div>
        <div class="flex flex-col gap-6">
          <div class="w-full flex justify-between items-center gap-2 pb-6 border-b-2 border-white">
            <span class="px-2.5 text-sm py-2 rounded-[10px] font-bold bg-primary">{{
              selectedStatement?.category
            }}</span>
            <span class="font-bold text-xl lg:text-3xl"
              >STELLING - {{ selectedStatement?.id }}</span
            >
          </div>
          <div class="flex flex-col gap-4 bg-background p-4 rounded-lg">
            <p class="text-xl lg:text-[28px] font-bold">{{ selectedStatement?.statement }}</p>
            <p class="text-md opacity-80 lg:text-lg">{{ selectedStatement?.explanation }}</p>
          </div>
        </div>
        <div class="flex flex-col gap-4">
          <span class="font-bold text-[28px]">Hiermee ben ik het</span>
          <div class="flex flex-wrap items-center gap-3 text-lg lg:flex-row">
            <button
              @click="saveAnswer(selectedStatement!.id, 'EENS')"
              :class="selectedStatement?.answer === 'EENS' ? 'bg-[#277D00]' : ''"
              class="btn opinion-btn !border-[#277D00] hover:bg-[#277D00]"
            >
              EENS
            </button>
            <span class="text-2xl">/</span>
            <button
              @click="saveAnswer(selectedStatement!.id, 'NEUTRAAL')"
              :class="selectedStatement?.answer === 'NEUTRAAL' ? 'bg-white text-black' : ''"
              class="btn opinion-btn hover:bg-white hover:text-black"
            >
              NEUTRAAL
            </button>
            <span class="text-2xl">/</span>
            <button
              @click="saveAnswer(selectedStatement!.id, 'ONEENS')"
              :class="selectedStatement?.answer === 'ONEENS' ? 'bg-[#FF1E00]' : ''"
              class="btn opinion-btn !border-[#FF1E00] hover:bg-[#FF1E00]"
            >
              ONEENS
            </button>
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
