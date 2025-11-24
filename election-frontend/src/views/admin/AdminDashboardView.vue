<template>
  <AdminLayout>
    <h1 class="admin-title">Admin Dashboard</h1>

    <div class="cards">
      <div class="card">
        <h2>Gebruikers</h2>
        <p>{{ stats.totalUsers }}</p>
      </div>

      <div class="card">
        <h2>Gemelde berichten</h2>
        <p>{{ stats.reportedPosts }}</p>
      </div>

      <div class="card">
        <h2>Openstaande reviews</h2>
        <p>{{ stats.pendingReviews }}</p>
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

<style scoped>
.admin-title {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 30px;
}

.cards {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.card {
  background: rgba(255, 255, 255, 0.9);
  padding: 25px;
  border-radius: 12px;
  width: 260px;
  text-align: center;
  backdrop-filter: blur(4px);
  color: #141c36;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 18px rgba(255, 255, 255, 0.2);
}
</style>
