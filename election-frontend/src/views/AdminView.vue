<template>
  <div class="admin-wrapper">
    <div class="admin-container">
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { AdminStats } from "../types/AdminStats";
import { getAdminStats } from "../services/adminService";

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
/* Achtergrond zoals de rest van je website */
.admin-wrapper {
  background-color: #141c36; /* Donkerblauw zoals jouw screenshot */
  min-height: 100vh;
  padding-top: 40px;
}

/* Container styling */
.admin-container {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  color: white;
  font-family: "Inter", sans-serif;
}

/* Titel styling */
.admin-title {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 30px;
}

/* Cards layout */
.cards {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

/* Elke card */
.card {
  background: rgba(255, 255, 255, 0.9);
  padding: 25px;
  border-radius: 12px;
  width: 260px;
  text-align: center;
  -webkit-backdrop-filter: blur(4px);
  backdrop-filter: blur(4px);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

/* Hover effect */
.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 18px rgba(255, 255, 255, 0.2);
}

/* Card titel */
.card h2 {
  margin-bottom: 12px;
  font-size: 1.2rem;
  font-weight: 600;
  color: #141c36;
}

/* Card waarde */
.card p {
  font-size: 2rem;
  font-weight: bold;
  color: #141c36;
}
</style>
