<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter()

const email = ref("");
const password = ref("");

const errors = reactive({
    email: "",
    password: "",
});

function isValidEmail(): void {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regex.test(email.value)) {
        errors.email = "Voer een geldig email adres in.";
    } else if (!email.value) {
        errors.email = "Email is verplicht."
    } else {
        errors.email = "";
    }
}

async function login(): Promise<void> {
    isValidEmail();
    errors.password = !password.value ? "Wachtwoord is verplicht." : "";

    if ((errors.email || errors.password) !== "") return;

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
            <div class="flex flex-col">
                <label for="Email" class="sr-only">Email</label>
                <input v-model="email" @input="errors.email = ''" type="text" name="email" id="" placeholder="Email"
                    class="outline-[#191919] outline-[8px] rounded-t-[10px] border-[1px] border-[#262626]
                    text-[20px] text-white bg-[#141414] py-2 px-4 placeholder-[#676767]" />
                <span v-if="errors.email" class="text-red-500 text-sm mt-3">
                    {{ errors.email }}</span>
            </div>
            <div class="flex flex-col">
                <label for="Password" class="sr-only">Password</label>
                <input v-model="password" @input="errors.password = ''" type="password" name="password" id=""
                    placeholder="Wachtwoord" class="outline-[#191919] outline-[8px] rounded-t-[10px] border-[1px] border-[#262626]
                    text-[20px] text-white bg-[#141414] py-2 px-4 placeholder-[#676767]" />
                <span v-if="errors.password" class="text-red-500 text-sm mt-3">
                    {{ errors.password }}</span>
            </div>
            <button type="submit" class="bg-[#EF3054] text-white p-3 text-[16px] rounded-[8px] cursor-pointer">Log
                in</button>
        </div>
    </form>
</template>
