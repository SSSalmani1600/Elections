import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import ElectionsView from '@/views/ElectionsView.vue'
import RegisterView from '@/views/RegisterView.vue'
import PartyDetailView from '@/views/PartyDetailView.vue'
import PartiesView from '@/views/PartiesView.vue'
import DiscussionsView from '@/views/DiscussionsView.vue'
import DiscussionDetailView from '@/views/DiscussionDetailView.vue'
import ElectionCalendarView from '@/views/ElectionCalenderView.vue'
import VotingGuideView from '@/views/VotingGuideView.vue'
import { authStore } from '@/store/authStore'
import AccountView from '@/views/AccountView.vue'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'

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
      name: 'partyDetail',
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
      path: '/stemwijzer',
      name: 'voting-guide',
      component: VotingGuideView,
    },
    {
      path: '/account',
      name: 'account',
      component: AccountView,
    },
    {
      path: '/admin/stats',
      name: 'admin',
      component: AdminDashboardView,
    },
    {
      path: '/admin',
      name: 'admin-dashboard',
      component: () => import('@/views/admin/AdminDashboardView.vue'),
    },

    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('@/views/admin/AdminUsersView.vue'),
    },

    {
      path: '/admin/moderation',
      name: 'admin-moderation',
      component: () => import('@/views/admin/AdminModerationView.vue'),
    },

    {
      path: '/admin/positions',
      name: 'admin-positions',
      component: () => import('@/views/admin/AdminPositionsView.vue'),
    },
    {
      path: '/stemwijzer',
      name: 'voting-guide',
      component: VotingGuideView,
    },
    {
      path: '/account',
      name: 'account',
      component: AccountView,
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  if (!authStore.state.initialized && !authStore.state.loading) {
    await authStore.initAuth()
  }

  if (to.meta.requiresAuth && !authStore.state.user) {
    return next({ name: 'login' })
  }

  if (to.name === 'login' && authStore.state.user) {
    return next({ name: 'home' })
  }

  next()
})

export default router
