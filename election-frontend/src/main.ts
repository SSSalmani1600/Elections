import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { authStore } from './store/authStore'

const app = createApp(App)

authStore.initAuth().finally(() => {
  app.use(PrimeVue, { theme: { preset: Aura } })
  app.use(router)

  app.mount('#app')
})
