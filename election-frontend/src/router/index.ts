import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import ElectionsView from '@/views/ElectionsView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PartyDetailView from '@/views/PartyDetailView.vue'
import AffiliationsView from "@/views/AffiliationsView.vue";

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
      name: 'affiliations',
      component: AffiliationsView,
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
  ],
})

export default router

