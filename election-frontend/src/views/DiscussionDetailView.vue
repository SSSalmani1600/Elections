← Terug naar forum
</button>

<!-- Loading / Error -->
<div v-if="loading" class="text-lg">Laden…</div>
<div v-else-if="error" class="text-red-500 text-lg">{{ error }}</div>

<!-- Detail -->
<div v-else-if="discussion" class="bg-white/5 rounded-2xl p-6 border border-white/10">
<h1 class="text-3xl font-bold mb-2">{{ discussion.title }}</h1>
<p class="text-sm text-[--color-text-muted] mb-6">
  Door {{ discussion.author }} ·
  Laatste activiteit: {{ new Date(discussion.lastActivityAt).toLocaleString() }} ·
  Reacties: {{ discussion.reactionsCount }}
</p>

<article class="prose prose-invert max-w-none">
  <p class="whitespace-pre-line">{{ discussion.body }}</p>
</article>

<!-- Placeholder voor reactieslijst (later uitbreiden als je reacties-objecten hebt) -->
<div class="mt-10">
  <h2 class="text-2xl font-semibold mb-3">Reacties</h2>
  <p class="text-[--color-text-muted]">
    (Nog geen afzonderlijke reacties-objecten in backend — teller is {{ discussion.reactionsCount }}.)
  </p>
</div>
</div>
</section>
</main>
</template>

<style scoped>
.prose :where(p):not(:where(.not-prose *)) {
  line-height: 1.7;
}
</style>
        <script setup lang="ts">
        </script>
