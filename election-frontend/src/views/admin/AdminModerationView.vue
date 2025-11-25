<template>
  <AdminLayout>
    <h1 class="text-3xl font-bold mb-6">Moderatie</h1>

    <!-- SECTION: PENDING -->
    <h2 class="text-2xl font-semibold mb-3">Te beoordelen reacties</h2>

    <div v-if="pending.length === 0" class="text-gray-400 mb-8">
      Geen reacties die beoordeeld moeten worden 🎉
    </div>

    <div v-for="r in pending" :key="r.id" class="bg-[#0B132B] text-white p-4 rounded-lg mb-4 shadow">
      <p class="text-gray-300 text-sm mb-2">Reactie ID: {{ r.id }}</p>
      <p class="text-lg mb-4">{{ r.content }}</p>

      <div class="flex gap-3">
        <button
          @click="approve(r.id)"
          class="px-4 py-2 bg-green-600 hover:bg-green-700 rounded"
        >
          ✔ Goedkeuren
        </button>

        <button
          @click="reject(r.id)"
          class="px-4 py-2 bg-red-600 hover:bg-red-700 rounded"
        >
          ✖ Afkeuren
        </button>
      </div>
    </div>


    <!-- SECTION: FLAGGED -->
    <h2 class="text-2xl font-semibold mt-12 mb-3">Gevaarlijke reacties (automatische detectie)</h2>

    <div v-if="flagged.length === 0" class="text-gray-400 mb-8">
      Geen verdachte reacties 🔍
    </div>

    <div v-for="r in flagged" :key="r.id" class="bg-[#401111] text-white p-4 rounded-lg mb-4 shadow-lg border border-red-500">
      <p class="text-gray-300 text-sm mb-2">Reden: {{ r.flaggedReason }}</p>
      <p class="text-lg mb-4">{{ r.content }}</p>

      <div class="flex gap-3">
        <button
          @click="approve(r.id)"
          class="px-4 py-2 bg-green-600 hover:bg-green-700 rounded"
        >
          ✔ Goedkeuren
        </button>

        <button
          @click="reject(r.id)"
          class="px-4 py-2 bg-red-600 hover:bg-red-700 rounded"
        >
          ✖ Verwijderen
        </button>

        <button
          @click="flagManual(r.id)"
          class="px-4 py-2 bg-yellow-500 hover:bg-yellow-600 rounded"
        >
          ⚠ Markeer als ernstig
        </button>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import AdminLayout from "@/layouts/AdminLayout.vue";

import {
  getPendingReactions,
  getFlaggedReactions,
  approveReaction,
  rejectReaction,
  flagReaction
} from "@/services/ModerationService";

const pending = ref([]);
const flagged = ref([]);

async function load() {
  pending.value = await getPendingReactions();
  flagged.value = await getFlaggedReactions();
}

async function approve(id: number) {
  await approveReaction(id);
  load();
}

async function reject(id: number) {
  await rejectReaction(id);
  load();
}

async function flagManual(id: number) {
  const reason = prompt("Waarom markeer je dit als ernstig?");
  if (reason) {
    await flagReaction(id, reason);
    load();
  }
}

onMounted(load);
</script>
