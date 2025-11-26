<template>
  <AdminLayout>
    <h1 class="text-3xl font-bold text-white mb-8">Beheerderspaneel</h1>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">

      <!-- Card: Total Users -->
      <div class="bg-white/10 border border-white/20 p-6 rounded-xl shadow-lg backdrop-blur-md text-white text-center hover:scale-[1.02] transition">
        <h2 class="text-xl font-semibold mb-2">Gebruikers</h2>
        <p class="text-4xl font-bold">{{ stats.totalUsers }}</p>
      </div>

      <!-- Card: Reported Posts -->
      <div class="bg-white/10 border border-white/20 p-6 rounded-xl shadow-lg backdrop-blur-md text-white text-center hover:scale-[1.02] transition">
        <h2 class="text-xl font-semibold mb-2">Gemelde berichten</h2>
        <p class="text-4xl font-bold">{{ stats.reportedPosts }}</p>
      </div>

      <!-- Card: Pending Reviews -->
      <div class="bg-white/10 border border-white/20 p-6 rounded-xl shadow-lg backdrop-blur-md text-white text-center hover:scale-[1.02] transition">
        <h2 class="text-xl font-semibold mb-2">Openstaande reviews</h2>
        <p class="text-4xl font-bold">{{ stats.pendingReviews }}</p>
      </div>

    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import AdminLayout from "@/layouts/AdminLayout.vue";
import type { AdminStats } from "@/types/api";
import { getAdminStats } from "@/services/AdminService";

const stats = ref<AdminStats>({
  totalUsers: 0,
  reportedPosts: 0,
  pendingReviews: 0,
});

onMounted(async () => {
  stats.value = await getAdminStats();
});
</script>
