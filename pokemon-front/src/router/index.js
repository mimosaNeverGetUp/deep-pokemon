import {createRouter, createWebHistory} from 'vue-router'
import Ladder from '@/views/Ladder.vue'
import PlayerRecord from '@/views/PlayerRecord.vue'

const routes = [
    {
        path: '/player-record',
        name: 'PlayerRecord',
        component: PlayerRecord
    },
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