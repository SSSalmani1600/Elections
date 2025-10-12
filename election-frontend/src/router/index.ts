import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import ElectionsView from '@/views/ElectionsView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PartyDetailView from '@/views/PartyDetailView.vue'
import PartiesView from "@/views/PartiesView.vue";
import DiscussionsView from '@/views/DiscussionsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/inloggen',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/partijen',
      name: 'parties',
      component: PartiesView,
    },
    {
      path: '/verkiezingen',
      name: 'elections',
      component: ElectionsView,
    },
    {
      path: '/registreren',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/partij/:name',
      name: "partyDetail",
      component: PartyDetailView,
    },
      {
          path: '/forum',
          name: 'forum',
          component: DiscussionsView,
      },
  ],
})

export default router

