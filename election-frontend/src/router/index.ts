import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import ElectionsView from '@/views/ElectionsView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PartyDetailView from '@/views/PartyDetailView.vue'
import PartiesView from "@/views/PartiesView.vue";
import DiscussionsView from '@/views/DiscussionsView.vue'
import DiscussionDetailView from '@/views/DiscussionDetailView.vue'
import ElectionCalendarView from '@/views/ElectionCalenderView.vue'
import AdminView from '@/views/AdminView.vue'


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
      path: '/discussions',
      name: 'forum',
      component: DiscussionsView,
    },
    {
      path: '/discussions/:id',
      name: 'discussion-detail',
      component: DiscussionDetailView,
    },
      {
          path: '/forum',
          name: 'forum',
          component: DiscussionsView,
      },

    {
      path: '/calendar',
      name: 'calendar',
      component: ElectionCalendarView,
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
    },
  ],
})

export default router

