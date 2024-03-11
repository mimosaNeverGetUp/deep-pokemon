import { createRouter, createWebHistory } from 'vue-router'
import Ladder from '../components/Ladder.vue'
const routes = [
    {
        path: '/',
        name: 'Ladder',
        component: Ladder
    }
]
const router = createRouter({
    history: createWebHistory(),
    routes,
})
export default router