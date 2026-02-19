import {createRouter, createWebHistory} from 'vue-router'
import Ladder from '@/views/Ladder.vue'
import TeamSearch from '@/views/TeamSearch.vue'
import Teams from '@/views/Teams.vue'
import PlayerRecord from '@/views/Player.vue'
import Stats from '@/views/Stats.vue'
import BattleAnalysis from '@/views/BattleAnalysis.vue'
import SpecifyTeamSearch from '@/views/SpecifyTeamSearch.vue'


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
        path: '/a7b3c9d1e5f2a8b4c6d0e3f1a9b2c4d6e8f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4',
        name: 'SpecifyTeamSearch',
        component: SpecifyTeamSearch
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