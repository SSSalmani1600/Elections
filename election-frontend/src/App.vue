<script setup lang="ts">
import { RouterView } from "vue-router"
import { ref } from "vue"
import { useAuth } from "@/stores/useAuth"

const menuIsOpen = ref(false)
const { isLoggedIn, displayName, logout } = useAuth()
const userMenuOpen = ref(false)
const toggleUserMenu = () => (userMenuOpen.value = !userMenuOpen.value)

// menu openen/sluiten
const toggleMenu = () => {
    menuIsOpen.value = !menuIsOpen.value
}
</script>


<template>
    <nav class="h-[100px] bg-background flex items-center sticky top-0 shadow-lg']">
        <div class="grid grid-cols-12 w-full px-6 max-lg:flex max-lg:justify-between max-lg:gap-10">
            <!-- LOGO -->
            <div class="col-span-4">
                <router-link to="/" exact>
                    <img src="./assets/logo.svg" alt="Logo" />
                </router-link>
            </div>

            <!-- NAV LINKS (desktop) -->
            <div class="flex items-center justify-center gap-8 col-span-4 max-lg:hidden">
                <router-link to="/verkiezingen" class="nav-link">Verkiezingen</router-link>
                <router-link to="/partijen" class="nav-link">Partijen</router-link>
                <router-link to="/forum" class="nav-link">Forum</router-link>
            </div>

            <!-- AUTH LINKS (desktop) -->
            <div class="flex items-center col-span-4 justify-end gap-4 max-lg:hidden">
                <template v-if="!isLoggedIn">
                    <router-link to="/inloggen" class="btn btn-primary !py-[6px]">Inloggen</router-link>
                    <router-link to="/registreren" class="btn btn-secondary !py-[6px]">Registreren</router-link>
                </template>
                <template v-else>
                    <div class="relative">
                        <button
                            @click="toggleUserMenu"
                            class="flex items-center justify-center w-10 h-10 rounded-full border border-gray-500 text-white hover:border-[#EF3054] transition"
                        >
                            <i class="pi pi-user text-lg"></i>
                        </button>

                        <div
                            v-if="userMenuOpen"
                            class="absolute top-[60px] right-0 w-44 bg-surface border border-gray-700 rounded-lg shadow-lg py-2 z-50"
                        >
                            <router-link
                                to="/account"
                                class="block px-4 py-2 text-sm text-gray-200 hover:bg-gray-700"
                                @click="userMenuOpen = false"
                            >
                                Mijn account
                            </router-link>
                            <button
                                @click="logout"
                                class="block w-full text-left px-4 py-2 text-sm text-red-400 hover:bg-gray-700"
                            >
                                Uitloggen
                            </button>
                        </div>
                    </div>
                </template>

            </div>

            <!-- MENU KNOP (mobiel) -->
            <button type="button" @click="toggleMenu" class="text-primary cursor-pointer lg:hidden">
                <i class="pi pi-bars text-2xl"></i>
            </button>

            <!-- MOBIEL MENU -->
            <Transition
                enter-active-class="transition-transform duration-300 ease-in-out"
                enter-from-class="translate-x-full"
                enter-to-class="translate-x-0"
                leave-active-class="transition-transform duration-300 ease-in-out"
                leave-from-class="translate-x-0"
                leave-to-class="translate-x-full"
            >
                <div
                    v-if="menuIsOpen"
                    class="lg:hidden w-2/5 max-md:w-3/5 max-sm:w-[90%] z-10 h-screen flex bg-surface shadow-2xl fixed top-0 right-0"
                >
                    <div class="w-full h-full">
                        <button
                            @click="toggleMenu"
                            class="cursor-pointer text-primary text-2xl absolute top-4 left-4"
                        >
                            <i class="pi pi-times"></i>
                        </button>

                        <div class="flex flex-col gap-10 mt-28 ml-14">
                            <div class="flex flex-col items-start gap-8 col-span-4">
                                <router-link to="/elections" class="nav-link">Verkiezingen</router-link>
                                <router-link to="/parties" class="nav-link">Partijen</router-link>
                                <router-link to="/forum" class="nav-link">Forum</router-link>
                            </div>

                            <div class="flex flex-col items-start gap-4">
                                <template v-if="!isLoggedIn">
                                    <router-link
                                        to="/login"
                                        class="btn btn-primary !py-[6px]"
                                        @click="toggleMenu"
                                    >Inloggen</router-link>
                                    <router-link
                                        to="/register"
                                        class="btn btn-secondary !py-[6px]"
                                        @click="toggleMenu"
                                    >Registreren</router-link>
                                </template>
                                <template v-else>
                                    <span class="text-black">Hallo, {{ displayName }}</span>
                                    <button @click="logout" class="btn btn-secondary !py-[6px]">Uitloggen</button>
                                </template>
                            </div>
                        </div>
                    </div>
                </div>
            </Transition>

            <!-- OVERLAY -->
            <Transition
                enter-active-class="transition-opacity duration-300 ease-in-out"
                enter-from-class="opacity-0"
                enter-to-class="opacity-100"
                leave-active-class="transition-opacity duration-300 ease-in-out"
                leave-from-class="opacity-100"
                leave-to-class="opacity-0"
            >
                <div
                    v-if="menuIsOpen"
                    @click="toggleMenu"
                    class="bg-black/50 w-screen h-screen fixed top-0 left-0"
                ></div>
            </Transition>
        </div>
    </nav>

    <RouterView />
</template>

<style scoped>
</style>

