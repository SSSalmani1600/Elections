<template>
  <div class="flex min-h-screen bg-[#1C2541] text-white">
    <!-- Sidebar -->
    <aside class="w-64 bg-[#0B132B] p-6 hidden md:block">
      <h2 class="text-2xl font-bold mb-8">Menu</h2>

      <nav class="flex flex-col gap-4">
        <RouterLink to="/admin/stats" class="hover:text-[#EF3054]">Overzicht</RouterLink>
        <RouterLink to="/admin/moderation" class="hover:text-[#EF3054]">Moderatie</RouterLink>
        <RouterLink to="/admin/polls" class="hover:text-[#EF3054]">Stellingen</RouterLink>
      </nav>
    </aside>

    <!-- Content -->
    <main class="flex-1 p-8">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
// geen script nodig
</script>

<style scoped>
</style>
