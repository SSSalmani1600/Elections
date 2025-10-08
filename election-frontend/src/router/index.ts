import { createRouter, createWebHistory } from 'vue-router'
import { defineComponent, h } from 'vue'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import DiscussionsView from '@/views/DiscussionsView.vue' // ✅ nieuwe import

// Inline placeholders (die mag je laten staan)
const ForumView = defineComponent({
    name: 'ForumView',
    setup() {
        return () =>
            h('section', { class: 'p-6' }, [
                h('h1', { class: 'text-2xl font-bold mb-2' }, 'Forum'),
                h('p', null, 'Forum pagina komt hier.'),
            ])
    },
})

const ElectionsView = defineComponent({
    name: 'ElectionsView',
    setup() {
        return () =>
            h('section', { class: 'p-6' }, [
                h('h1', { class: 'text-2xl font-bold mb-2' }, 'Elections'),
                h('p', null, 'Overzicht van verkiezingen.'),
            ])
    },
})

const PartiesView = defineComponent({
    name: 'PartiesView',
    setup() {
        return () =>
            h('section', { class: 'p-6' }, [
                h('h1', { class: 'text-2xl font-bold mb-2' }, 'Parties'),
                h('p', null, 'Lijst met partijen.'),
            ])
    },
})

const NotFoundView = defineComponent({
    name: 'NotFoundView',
    setup() {
        return () =>
            h('section', { class: 'p-6' }, [
                h('h1', { class: 'text-2xl font-bold mb-2' }, '404 — Pagina niet gevonden'),
                h('p', null, [
                    h('span', null, 'Ga terug naar '),
                    h('a', { href: '/', class: 'underline' }, 'home'),
                ]),
            ])
    },
})

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', name: 'home', component: HomeView },
        { path: '/login', name: 'login', component: LoginView },

        // ✅ nieuwe route voor jouw DiscussionsView
        { path: '/discussions', name: 'discussions', component: DiscussionsView },

        // bestaande inline routes
        { path: '/forum', name: 'forum', component: ForumView },
        { path: '/elections', name: 'elections', component: ElectionsView },
        { path: '/parties', name: 'parties', component: PartiesView },

        // fallback
        { path: '/:pathMatch(.*)*', name: 'not-found', component: NotFoundView },
    ],
})

export default router
