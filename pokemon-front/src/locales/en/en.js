/*
 *  MIT License
 *
 *  Copyright (c) 2025-2025 mimosa
 */

import {en_page_pokemon_stat} from './en_page_pokemon_stat.js'
import {en_page_rank_stat} from './en_page_rank_stat.js'
import {en_page_meta_stat} from './en_page_meta_stat.js'
import {en_page_navi} from './en_page_navi.js'
import {en_page_team_search} from './en_page_team_search.js'
import {en_page_ladder} from './en_page_ladder.js'
import {en_page_player} from './en_page_player.js'
import {en_ps} from './en_ps.js'

export const en = {
    ...en_ps,
    ...en_page_pokemon_stat,
    ...en_page_meta_stat,
    ...en_page_navi,
    ...en_page_team_search,
    ...en_page_ladder,
    ...en_page_player,
    ...en_page_rank_stat
};