<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()

const goToElections = () => router.push('/elections')
const goToQuiz = () => router.push('/quiz')
const goToForum = () => router.push('/discussions')
</script>

<template>
    <main class="flex flex-col items-center bg-[--color-background] text-[--color-text-base] min-h-screen">
        <div class="flex flex-col items-center w-full mt-[75px] gap-8 px-6 flex-grow">
            <!-- Titel -->
            <div class="flex flex-col items-center gap-3 text-center">
                <h1 class="text-3xl font-bold">Maak kennis met de Nederlandse politiek</h1>
                <p class="text-[--color-text-muted] text-lg w-[60%] text-center">
                    <strong>Leer</strong> partijen kennen, <strong>bekijk</strong> de uitslagen en
                    <strong>ontdek</strong> jouw politieke match
                </p>
            </div>

            <!-- Knoppen -->
            <div class="flex gap-4">
                <button
                    class="px-6 py-3 rounded-lg font-semibold bg-[--color-primary] text-[--color-secondary] hover:opacity-90 transition"
                    @click="goToElections"
                >
                    Ontdek verkiezingen
                </button>

                <button
                    class="px-6 py-3 rounded-lg font-semibold border border-[--color-primary] text-[--color-primary] hover:bg-[--color-primary] hover:text-[--color-secondary] transition"
                    @click="goToQuiz"
                >
                    Doe de match quiz
                </button>

                <button
                    class="px-6 py-3 rounded-lg font-semibold border border-[--color-primary] text-[--color-primary] hover:bg-[--color-primary] hover:text-[--color-secondary] transition"
                    @click="goToForum"
                >
                    Forum
                </button>
            </div>
        </div>

        <!-- ✅ Grote footer -->
        <footer
            class="w-full bg-[color:rgb(12,20,40)] text-center py-16 mt-20 border-t border-[color:rgba(255,255,255,0.05)] flex flex-col items-center justify-center"
        >
            <h2 class="text-2xl font-bold text-[--color-primary] mb-4">Praat mee!</h2>
            <p class="text-base text-[--color-text-muted] max-w-[700px] px-6 leading-relaxed">
                In ons forum kun je jouw mening delen met anderen en ontdekken wat zij denken.
                Je vindt er bovendien handige informatie en nieuwe perspectieven.
            </p>
            <button
                @click="goToForum"
                class="mt-6 px-6 py-3 rounded-lg font-semibold bg-[--color-primary] text-[--color-secondary] hover:opacity-90 transition"
            >
                Ga naar het forum
            </button>
        </footer>
    </main>
</template>
