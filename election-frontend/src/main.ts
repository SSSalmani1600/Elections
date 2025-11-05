// main.ts
import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { createPinia } from 'pinia'
import { useAuthStore } from '@/store/useAuthStore'  // ✅ importeer je store

const app = createApp(App)
const pinia = createPinia()

app.use(PrimeVue, { theme: { preset: Aura } })
app.use(pinia)
app.use(router)


const auth = useAuthStore()
const savedToken = localStorage.getItem('JWT')
const savedUsername = localStorage.getItem('username')

if (savedToken && savedUsername) {
  auth.login(savedUsername, savedToken)
}

app.mount('#app')
