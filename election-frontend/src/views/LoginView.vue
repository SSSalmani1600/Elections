<script setup lang="ts">
import { useAuth } from '@/store/authStore'
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const agree = ref(false)
const email = ref('')
const password = ref('')
const { user, login, initialized } = useAuth();

const error = reactive({
  email: '',
  password: '',
})

// Check of gebruiker al ingelogd is
onMounted(() => {
  if (initialized.value && user.value) {
    router.replace('/')
  }
})

function isValidEmail(): void {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!regex.test(email.value)) {
    error.email = 'Voer een geldig email adres in.'
  } else if (!email.value) {
    error.email = 'Email is verplicht.'
  } else {
    error.email = ''
  }
}

async function loginHandler(): Promise<void> {
  isValidEmail()
  error.password = !password.value ? 'Wachtwoord is verplicht.' : ''

  if ((error.email || error.password) !== '') return

  try {
    await login(email.value, password.value)

    router.replace('/')
  } catch (err: unknown) {
    console.log(err)
    error.email = 'Inloggen mislukt. Controleer je gegevens.'
  }
}
</script>

<template>
  <div class="h-[calc(100vh-100px)] bg-[#1C2541] flex flex-col items-center justify-center px-4">
    <div class="bg-[#0B132B] p-8 rounded-2xl shadow-lg w-full max-w-md flex flex-col gap-6 text-white">
      <h1 class="text-3xl font-bold text-center">Inloggen</h1>

      <form @submit.prevent="loginHandler" class="flex flex-col gap-5">
        <!-- Email -->
        <div class="flex flex-col gap-2">
          <label for="email" class="text-sm font-medium text-gray-300">E-mail</label>
          <input v-model="email" @input="error.email = ''" type="email" id="email" name="email" autocomplete="email"
            class="bg-[#0c0f2a] border border-[#30335a] rounded-lg p-3 text-white focus:outline-none focus:ring-2 focus:ring-[#EF3054]"
            placeholder="E-mail" />
          <span v-if="error.email" class="text-[#EF3054] text-sm mt-1">{{ error.email }}</span>
        </div>

        <!-- Password -->
        <div class="flex flex-col gap-2">
          <label for="password" class="text-sm font-medium text-gray-300">Wachtwoord</label>
          <input v-model="password" @input="error.password = ''" type="password" id="password" name="password"
            autocomplete="current-password"
            class="bg-[#0c0f2a] border border-[#30335a] rounded-lg p-3 text-white focus:outline-none focus:ring-2 focus:ring-[#EF3054]"
            placeholder="Wachtwoord" />
          <span v-if="error.password" class="text-[#EF3054] text-sm mt-1">
            {{ error.password }}</span>
        </div>
        <div class="flex items-center gap-2 text-sm">
          <input id="agree" type="checkbox" v-model="agree" class="w-4 h-4 accent-[#EF3054]" />
          <label for="agree">
            Ik ga akkoord met de
            <span class="text-[#EF3054] cursor-pointer">voorwaarden</span>
            en
            <span class="text-[#EF3054] cursor-pointer">privacyverklaring</span>.
          </label>
        </div>
        <!-- Submit -->
        <button type="submit"
          class="bg-[#EF3054] hover:bg-[#D9294B] text-white py-3 rounded-lg font-semibold shadow-md hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-[#EF3054]/40 transition-all duration-300">
          Log in
        </button>
      </form>

      <!-- Footer -->
      <p class="text-sm text-gray-300 text-center">
        Nog geen account?
        <router-link to="/registreren" class="text-[#EF3054] hover:underline">Registreren</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped></style>
