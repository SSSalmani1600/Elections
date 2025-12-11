import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import Toast from 'primevue/toast'
import ToastService from 'primevue/toastservice'
import Aura from '@primeuix/themes/aura'
import { authStore } from './store/authStore'

const app = createApp(App)

authStore.initAuth().finally(() => {
  app.use(PrimeVue, { theme: { preset: Aura } })
  app.use(ToastService)
  app.use(router)

  app.component('Toast', Toast);

  app.mount('#app')
})
