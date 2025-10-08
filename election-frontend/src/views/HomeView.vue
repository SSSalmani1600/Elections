<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToElections = () => router.push('/elections')
const goToQuiz = () => router.push('/quiz') // pas aan als je route anders heet
const goToForum = () => router.push('/discussions')
</script>

<template>
    <main class="flex flex-col items-center">
        <div class="flex flex-col items-center w-fit mt-[75px] gap-8">
            <div class="flex flex-col items-center gap-3">
                <h1>Maak kennis met de Nederlandse politiek</h1>
                <p class="text-text-muted text-lg w-[60%] text-center">
                    <strong>Leer</strong> partijen kennen, <strong>bekijk</strong> de uitslagen en
                    <strong>ontdek</strong> jouw politieke match
                </p>
            </div>

            <div class="flex gap-4">
                <button class="btn btn-primary" @click="goToElections">Ontdek verkiezingen</button>
                <button class="btn btn-secondary" @click="goToQuiz">Doe de match quiz</button>
                <!-- ✅ Forum knop -->
                <button class="btn btn-secondary" @click="goToForum">Forum</button>
            </div>
        </div>
    </main>
</template>
