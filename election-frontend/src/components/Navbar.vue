<script setup lang="ts">
import { useAuth } from '@/store/authStore'
import { ref, onMounted, onBeforeUnmount } from 'vue'

const menuIsOpen = ref(false)
const userMenuOpen = ref(false)
const userMenuRef = ref<HTMLElement | null>(null)


const { user, logout } = useAuth();

const toggleMenu = () => {
  menuIsOpen.value = !menuIsOpen.value
}
const toggleUserMenu = () => {
  userMenuOpen.value = !userMenuOpen.value
}

const handleClickOutside = (event: MouseEvent) => {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target as Node)) {
    userMenuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <nav class="h-[100px] bg-background flex items-center justify-between px-6 sticky top-0 shadow-lg z-100">

    <router-link to="/" class="flex items-center gap-2">
      <img src="../assets/logo.svg" alt="Logo" class="h-8" />
    </router-link>

    <div class="hidden lg:flex items-center justify-center gap-8">
      <router-link to="/verkiezingen" class="nav-link">Verkiezingen</router-link>
      <router-link to="/partijen" class="nav-link">Partijen</router-link>
      <router-link to="/forum" class="nav-link">Forum</router-link>
      <router-link to="/calendar" class="nav-link">Verkiezingskalender</router-link>
      <router-link to="/stemwijzer" class="nav-link">Stemwijzer</router-link>
    </div>

    <div class="relative flex items-center gap-4 max-lg:hidden" ref="userMenuRef">
      <template v-if="!user">
        <router-link to="/inloggen" class="btn btn-primary !py-[6px]">Inloggen</router-link>
        <router-link to="/registreren" class="btn btn-secondary !py-[6px]">Registreren</router-link>
      </template>


      <template v-else>

        <button @click.stop="toggleUserMenu"
          class="flex items-center justify-center w-10 h-10 rounded-full border cursor-pointer border-gray-500 text-white hover:border-[#EF3054] transition">
          <i class="pi pi-user text-lg"></i>
        </button>


        <Transition enter-active-class="transition ease-out duration-200" enter-from-class="opacity-0 translate-y-1"
          enter-to-class="opacity-100 translate-y-0" leave-active-class="transition ease-in duration-150"
          leave-from-class="opacity-100 translate-y-0" leave-to-class="opacity-0 translate-y-1">
          <div v-if="userMenuOpen"
            class="absolute top-full right-0 mt-3 w-48 bg-[#1c1f2b] shadow-lg rounded-lg border border-gray-700 z-50">
            <p class="px-4 py-2 text-sm text-gray-300 border-b border-gray-600">
              👋 Hallo, <strong>{{ user.username }}</strong>
            </p>


            <router-link v-if="user.isAdmin" to="/admin"
              class="block px-4 py-2 text-sm text-yellow-400 hover:bg-gray-700" @click="userMenuOpen = false">
              Beheerderspaneel
            </router-link>

            <router-link to="/account" class="block px-4 py-2 text-sm text-gray-200 hover:bg-gray-700"
              @click="userMenuOpen = false">
              Mijn account
            </router-link>

            <button @click="logout"
              class="block w-full cursor-pointer text-left px-4 py-2 text-sm text-red-400 hover:bg-gray-700">
              Uitloggen
            </button>
          </div>
        </Transition>
      </template>
    </div>


    <button type="button" @click="toggleMenu" class="text-white cursor-pointer lg:hidden">
      <i class="pi pi-bars text-2xl"></i>
    </button>

    <Transition enter-active-class="transition-transform duration-300 ease-in-out" enter-from-class="translate-x-full"
      enter-to-class="translate-x-0" leave-active-class="transition-transform duration-300 ease-in-out"
      leave-from-class="translate-x-0" leave-to-class="translate-x-full">
      <div v-if="menuIsOpen"
        class="lg:hidden w-3/5 max-md:w-3/4 z-10 h-screen flex bg-surface shadow-2xl fixed top-0 right-0">
        <div class="w-full h-full p-6 flex flex-col gap-6">
          <button @click="toggleMenu" class="text-primary text-2xl self-start cursor-pointer">
            <i class="pi pi-times"></i>
          </button>

          <router-link to="/verkiezingen" class="nav-link" @click="toggleMenu">Verkiezingen</router-link>
          <router-link to="/partijen" class="nav-link" @click="toggleMenu">Partijen</router-link>
          <router-link to="/forum" class="nav-link" @click="toggleMenu">Forum</router-link>
          <router-link to="/calendar" class="nav-link" @click="toggleMenu">Verkiezingskalender</router-link>
          <router-link to="/stemwijzer" class="nav-link" @click="toggleMenu">Stemwijzer</router-link>
          <template v-if="!user">
            <router-link to="/inloggen" class="btn btn-primary" @click="toggleMenu">Inloggen</router-link>
            <router-link to="/registreren" class="btn btn-secondary" @click="toggleMenu">Registreren</router-link>
          </template>


          <template v-else>
            <p class="text-gray-200">👋 Hallo, {{ user.username }}</p>
            <router-link to="/account" class="btn btn-primary" @click="toggleMenu">Mijn account</router-link>
            <button @click="logout" class="btn btn-secondary cursor-pointer">Uitloggen</button>
          </template>
        </div>
      </div>
    </Transition>

    <Transition enter-active-class="transition-opacity duration-300 ease-in-out" enter-from-class="opacity-0"
      enter-to-class="opacity-100" leave-active-class="transition-opacity duration-300 ease-in-out"
      leave-from-class="opacity-100" leave-to-class="opacity-0">
      <div v-if="menuIsOpen" @click="toggleMenu"
        class="bg-black/50 w-screen h-screen fixed top-0 left-0 hidden max-lg:block"></div>
    </Transition>

  </nav>
</template>

<style scoped></style>
