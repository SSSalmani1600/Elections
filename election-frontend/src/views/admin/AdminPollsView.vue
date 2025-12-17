<script setup lang="ts">
import { ref } from "vue";
import AdminLayout from "@/layouts/AdminLayout.vue";
import { createPoll } from "@/services/PollService";

const question = ref("");
const error = ref("");
const success = ref("");
const loading = ref(false);

async function submit() {
  error.value = "";
  success.value = "";

  if (!question.value.trim()) {
    error.value = "De vraag mag niet leeg zijn.";
    return;
  }

  loading.value = true;
  try {
    await createPoll(question.value);
    success.value = "Nieuwe stelling succesvol geplaatst.";
    question.value = "";
  } catch (e: any) {
    error.value = e.message || "Opslaan mislukt";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <AdminLayout>
    <h1 class="text-3xl font-bold mb-6 text-white">Stelling toevoegen</h1>

    <p v-if="error" class="text-red-400 mb-3">{{ error }}</p>
    <p v-if="success" class="text-green-400 mb-3">{{ success }}</p>

    <textarea
      v-model="question"
      rows="3"
      class="w-full p-3 rounded-lg bg-white/10 text-white"
      placeholder="Voer hier de stelling in..."
    />

    <button
      class="btn btn-primary mt-4"
      :disabled="loading"
      @click="submit"
    >
      {{ loading ? "Opslaan..." : "Plaatsen" }}
    </button>
  </AdminLayout>
</template>
