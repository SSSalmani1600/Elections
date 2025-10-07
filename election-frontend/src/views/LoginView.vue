<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter()

const email = ref("");
const password = ref("");

async function login(): Promise<void> {
    const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email.value,
            password: password.value
        })
    })

    if (res.status === 200) {
        router.resolve("/home");
    }
}
</script>

<template>
    <form @submit.prevent="login()" class="p-4 flex flex-col justify-around items-center p-24">
        <h1 class="text-white text-3xl pb-8">Inloggen</h1>
        <div class="flex flex-col gap-8 w-[500px]">
            <div class="flex flex-col gap-1">
                <label for="Email" class="text-black sr-only">Email</label>
                <input v-model="email" type="text" name="email" id="" placeholder="Email" class="outline-[#191919] outline-[8px] rounded-t-[10px] border-[1px] border-[#262626]
                    text-[20px] text-white bg-[#141414] py-2 px-4 placeholder-[#676767]" />
            </div>
            <div class="flex flex-col gap-1">
                <label for="Password" class="text-black sr-only">Password</label>
                <input v-model="password" type="password" name="password" id="" placeholder="Wachtwoord" class="outline-[#191919] outline-[8px] rounded-t-[10px] border-[1px] border-[#262626]
                    text-[20px] text-white bg-[#141414] py-2 px-4 placeholder-[#676767]" />
            </div>
            <button class="bg-[#EF3054] text-white p-3 text-[16px] rounded-[8px] cursor-pointer">Log
                in</button>
        </div>
    </form>
</template>
