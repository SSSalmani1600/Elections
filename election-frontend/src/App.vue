<script setup lang="ts">
import {RouterView} from 'vue-router'
</script>

<template>
  <nav class="h-[100px] bg-background flex items-center">
    <div class="grid grid-cols-12 w-full px-6">
      <div class="col-span-4">
        <router-link to="/" exact>
          <img src="./assets/logo.svg" alt="Logo">
        </router-link>
      </div>
      <div class="flex items-center justify-center gap-8 col-span-4">
        <router-link to="/elections" class="nav-link">Verkiezingen</router-link>
        <router-link to="/parties" class="nav-link">Partijen</router-link>
        <router-link to="/forum" class="nav-link">Forum</router-link>
      </div>
      <div class="flex items-center col-span-4 justify-end gap-4">
        <a type="button" href="/login" class="btn btn-primary !py-[6px]">Inloggen</a>
        <a type="button" href="/register" class="btn btn-secondary !py-[6px]">Registreren</a>
      </div>
    </div>
  </nav>
  <RouterView/>
</template>

<style scoped></style>
