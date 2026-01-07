<script setup lang="ts">
import { ref, onMounted } from "vue";
import { getAdminStats } from "@/services/AdminService";
import type {AdminStats} from '@/types/api.ts'

const stats = ref<AdminStats>({
  totalUsers: 0,
  reportedPosts: 0,
  pendingReviews: 0,
});

onMounted(async () => {
  stats.value = await getAdminStats();
});
</script>

<template>
  <div class="min-h-[calc(100vh-100px)] bg-[#1C2541] flex flex-col items-center px-4 py-10">
    <div class="w-full max-w-6xl">

      <!-- Titel -->
      <h1 class="text-3xl font-bold text-white mb-8 text-center">
        Admin Dashboard
      </h1>

      <!-- Cards container -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">

        <!-- Gebruikers -->
        <div
          class="bg-[#0B132B] p-6 rounded-xl shadow-lg text-white hover:shadow-2xl hover:scale-[1.02] transition-all duration-300"
        >
          <h2 class="text-xl font-semibold text-gray-300 mb-2">Totaal aantal gebruikers</h2>
          <p class="text-4xl font-bold text-[#EF3054]">{{ stats.totalUsers }}</p>
        </div>

        <!-- Gemelde berichten -->
        <div
          class="bg-[#0B132B] p-6 rounded-xl shadow-lg text-white hover:shadow-2xl hover:scale-[1.02] transition-all duration-300"
        >
          <h2 class="text-xl font-semibold text-gray-300 mb-2">Gemelde berichten</h2>
          <p class="text-4xl font-bold text-[#EF3054]">{{ stats.reportedPosts }}</p>
        </div>


        <div
          class="bg-[#0B132B] p-6 rounded-xl shadow-lg text-white hover:shadow-2xl hover:scale-[1.02] transition-all duration-300"
        >
          <h2 class="text-xl font-semibold text-gray-300 mb-2">Openstaande reviews</h2>
          <p class="text-4xl font-bold text-[#EF3054]">{{ stats.pendingReviews }}</p>
        </div>

      </div>


      <div class="mt-12 grid grid-cols-1 sm:grid-cols-3 gap-6">

        <router-link
          to="/admin/moderation"
          class="bg-[#EF3054] text-white text-center py-4 rounded-xl font-semibold shadow-md hover:bg-[#D9294B] transition-all"
        >
          Moderatie
        </router-link>

        <router-link
          to="/admin/polls"
          class="bg-[#EF3054] text-white text-center py-4 rounded-xl font-semibold"
        >
          Stellingen beheren
        </router-link>

      </div>

    </div>
  </div>
</template>

<style scoped></style>
