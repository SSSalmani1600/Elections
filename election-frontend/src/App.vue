<script setup lang="ts">
import {RouterView} from 'vue-router'
import {ref} from "vue";

const menuIsOpen = ref(false);

const toggleMenu = () => {
  menuIsOpen.value = !menuIsOpen.value;
}
</script>

<template>
  <nav class="h-[100px] bg-background flex items-center">
    <div class="grid grid-cols-12 w-full px-6 max-lg:flex max-lg:justify-between max-lg:gap-10">
      <div class="col-span-4">
        <router-link to="/" exact>
          <img src="./assets/logo.svg" alt="Logo">
        </router-link>
      </div>
      <div class="flex items-center justify-center gap-8 col-span-4 max-lg:hidden">
        <router-link to="/elections" class="nav-link">Verkiezingen</router-link>
        <router-link to="/parties" class="nav-link">Partijen</router-link>
        <router-link to="/forum" class="nav-link">Forum</router-link>
      </div>
      <div class="flex items-center col-span-4 justify-end gap-4 max-lg:hidden">
        <a type="button" href="/login" class="btn btn-primary !py-[6px]">Inloggen</a>
        <a type="button" href="/register" class="btn btn-secondary !py-[6px]">Registreren</a>
      </div>
      <button type="button" @click="toggleMenu" class="text-primary cursor-pointer lg:hidden"><i
        class="pi pi-bars text-2xl"></i></button>
      <Transition
        enter-active-class="transition-transform duration-300 ease-in-out"
        enter-from-class="translate-x-full"
        enter-to-class="translate-x-0"
        leave-active-class="transition-transform duration-300 ease-in-out"
        leave-from-class="translate-x-0"
        leave-to-class="translate-x-full"
      >
        <div v-if="menuIsOpen"
             class="lg:hidden w-2/5 max-md:w-3/5 max-sm:w-[90%] z-10 h-screen flex bg-surface shadow-2xl fixed top-0 right-0">
          <div class="w-full h-full">

            <button @click="toggleMenu"
                    class="cursor-pointer text-primary text-2xl absolute top-4 left-4"><i
              class="pi pi-times"></i></button>
            <div class="flex flex-col gap-10 mt-28 ml-14">
              <div class="flex flex-col items-start gap-8 col-span-4">
                <router-link to="/elections" class="nav-link">Verkiezingen</router-link>
                <router-link to="/parties" class="nav-link">Partijen</router-link>
                <router-link to="/forum" class="nav-link">Forum</router-link>
              </div>
              <div class="flex items-center gap-4">
                <a type="button" href="/login" class="btn btn-primary !py-[6px]">Inloggen</a>
                <a type="button" href="/register"
                   class="btn btn-secondary !py-[6px]">Registreren</a>
              </div>
            </div>
          </div>
        </div>
      </Transition>
      <Transition
        enter-active-class="transition-opacity duration-300 ease-in-out"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-active-class="transition-opacity duration-300 ease-in-out"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div v-if="menuIsOpen" @click="toggleMenu" class="bg-black/50 w-screen h-screen fixed top-0 left-0"></div>
      </Transition>
    </div>
  </nav>
  <RouterView/>
</template>

<style scoped></style>
