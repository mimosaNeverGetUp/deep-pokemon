import {createRouter, createWebHistory} from 'vue-router'
import Ladder from '@/views/Ladder.vue'
import TeamSearch from '@/views/TeamSearch.vue'
import Teams from '@/views/Teams.vue'
import PlayerRecord from '@/views/Player.vue'
import Stats from '@/views/Stats.vue'
import BattleAnalysis from '@/views/BattleAnalysis.vue'


const routes = [
    {
        path: '/player-record',
        name: 'PlayerRecord',
        component: PlayerRecord
    },
    {
        path: '/ladder',
        name: 'Ladder',
        component: Ladder
    },
    {
        path: '/teamSearch',
        name: 'TeamSearch',
        component: TeamSearch
    },
    {
        path: '/teams',
        name: 'Teams',
        component: Teams
    },
    {
        path: '/stats',
        name: 'Stats',
        component: Stats
    },
    {
        path: '/battleAnalysis',
        name: 'BattleAnalysis',
        component: BattleAnalysis
    },
    {
        path: '/',
        name: 'default',
        component: Ladder
    }
]
const router = createRouter({
    history: createWebHistory(),
    routes,
})
export default router