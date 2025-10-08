<script setup lang="ts">

const discussions = [
    { id: 1, title: 'Verkiezing 2025 debat', author: 'Nabil', replies: 12 },
    { id: 2, title: 'Welke partij verdient jouw stem?', author: 'Fatima', replies: 5 },
    { id: 3, title: 'Hoe werkt de zetelverdeling?', author: 'Joris', replies: 8 },
]
</script>

<template>
    <main class="flex flex-col items-center bg-[--color-background] text-[--color-text-base] min-h-screen">
        <div class="flex flex-col items-center w-full mt-[75px] gap-8 px-6 max-w-5xl">
            <div class="flex flex-col items-center gap-3 text-center">
                <h1 class="text-3xl font-bold">Forum Discussies</h1>
                <p class="text-[--color-text-muted] text-lg max-w-[600px]">
                    <strong>Lees</strong> mee met de gesprekken, <strong>deel</strong> je mening en
                    <strong>ontdek</strong> wat anderen denken over de verkiezingen
                </p>
            </div>

            <div class="flex flex-col w-full gap-6 mt-4">
                <div
                    v-for="d in discussions"
                    :key="d.id"
                    class="p-6 rounded-2xl bg-[--color-surface] shadow hover:shadow-lg transition"
                >
                    <h2 class="text-xl font-semibold text-[--color-primary] mb-2">
                        {{ d.title }}
                    </h2>
                    <p class="text-sm text-[--color-text-muted]">
                        door <span class="font-medium">{{ d.author }}</span> • {{ d.replies }} reacties
                    </p>
                </div>
            </div>

            <div class="mt-8">
                <button class="px-6 py-3 rounded-lg font-semibold bg-[--color-primary] text-[--color-secondary] hover:opacity-90 transition">
                    Nieuwe discussie starten
                </button>
            </div>
        </div>
    </main>
</template>
