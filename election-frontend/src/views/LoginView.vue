<<<<<<< election-frontend/src/views/LoginView.vue
<script setup lang="ts">
import { login } from '@/services/AuthService'
import type { LoginResponse } from '@/types/api'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const email = ref('')
const password = ref('')

const error = reactive({
    email: '',
    password: '',
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
        const data: LoginResponse = await login(email.value, password.value)
        localStorage.setItem('JWT', data.token)
        router.replace('/')
    } catch (err: unknown) {
        console.log(err)
        error.email = 'Inloggen mislukt. Controleer je gegevens.'
    }
}
</script>

<template>
    <form @submit.prevent="loginHandler()" class="flex flex-col justify-around items-center p-24">
        <h1 class="text-white text-3xl pb-8">Inloggen</h1>
        <div class="flex flex-col gap-8 w-[500px]">
            <div class="flex flex-col">
                <label for="Email" class="sr-only">Email</label>
                <input
                    v-model="email"
                    @input="error.email = ''"
                    type="text"
                    name="email"
                    id=""
                    placeholder="Email"
                    class="outline-[#191919] outline-[8px] rounded-t-[10px] border-[1px] border-[#262626] text-[20px] text-white bg-[#141414] py-2 px-4 placeholder-[#676767]"
                />
                <span v-if="error.email" class="text-red-500 text-sm mt-3"> {{ error.email }}</span>
            </div>
            <div class="flex flex-col">
                <label for="Password" class="sr-only">Password</label>
                <input
                    v-model="password"
                    @input="error.password = ''"
                    type="password"
                    name="password"
                    id=""
                    placeholder="Wachtwoord"
                    class="outline-[#191919] outline-[8px] rounded-t-[10px] border-[1px] border-[#262626] text-[20px] text-white bg-[#141414] py-2 px-4 placeholder-[#676767]"
                />
                <span v-if="error.password" class="text-red-500 text-sm mt-3">
                    {{ error.password }}</span
                >
            </div>
            <button
                type="submit"
                class="bg-[#EF3054] text-white p-3 text-[16px] rounded-[8px] cursor-pointer w-[90%] self-center"
            >
                Log in
            </button>
        </div>
    </form>
</template>

<style scoped></style>
