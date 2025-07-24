/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

import {zh_ps} from './zh_ps.js'
import {zh_page_pokemon_stat} from './zh_page_pokemon_stat.js'
import {zh_page_rank_stat} from './zh_page_rank_stat.js'
import {zh_page_meta_stat} from './zh_page_meta_stat.js'
import {zh_page_team_search} from './zh_page_team_search.js'
import {zh_page_navi} from './zh_page_navi.js'
import {zh_page_ladder} from './zh_page_ladder.js'
import {zh_page_player} from './zh_page_player.js'

export const zh = {
    ...zh_ps,
    ...zh_page_rank_stat,
    ...zh_page_meta_stat,
    ...zh_page_navi,
    ...zh_page_team_search,
    ...zh_page_ladder,
    ...zh_page_player,
    ...zh_page_pokemon_stat
}