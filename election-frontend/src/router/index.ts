import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import ElectionsView from '@/views/ElectionsView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PartyDetailView from '@/views/PartyDetailView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
        },
        {
            path: '/about',
            name: 'about',
            // route level code-splitting
            // this generates a separate chunk (About.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('../views/AboutView.vue'),
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView,
        },
        {
            path: '/elections',
            name: 'elections',
            component: ElectionsView,
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterView,
        },
      {
        path: '/partyDetail/:name',
        name: "partyDetail",
        component: PartyDetailView,
      },
    ],
})

export default router

