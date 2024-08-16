<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref, watch} from "vue";

import Divider from 'primevue/divider';
import ProgressSpinner from 'primevue/progressspinner';
import UsageDif from "@/components/stats/UsageDif.vue";
import {abilityText} from "@/components/data/abilityText.js";
import {itemText} from "@/components/data/ItemText.js";
import {moveText} from "@/components/data/moveText.js";
import {moveInfo} from "@/components/data/moveInfo.js";
import {nature} from "@/components/data/nature.js";
import Team from "@/components/Team.vue";

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const props = defineProps({
  pokemon: Object,
  format: String
})
const moveset = ref()
const sets = ref()
const teams = ref(null)

async function fetchStatsData(format, pokemon) {
  moveset.value = null
  const res = await fetch(`${apiUrl}/api/stats/${format}/moveset/` + pokemon, {
        method: "GET"
      }
  );
  moveset.value = await res.json()
}

function showDefaultIcon(event) {
  event.target.src = getIconUrl(props.pokemon.name);
}

function getIconUrl(pokemon) {
  const iconName = pokemon.replace(" ", "").replace("-*", "")
  return "/pokemonicon/" + iconName + ".png"
}

watch(() => props.pokemon, async (newPokemon) => {
  await fetchStatsData(props.format, newPokemon.name);
  await queryTeams(0, 5, newPokemon.name);
  await queryPokemonSet(props.format, newPokemon.name);
})

function convertToPercentage(f) {
  return (f * 100).toFixed(2) + '%'
}

function filterPopularSet(set, thresold) {
  let tmp = {}
  for (let key in set) {
    if (set[key] >= thresold) {
      tmp[key] = set[key];
    }
  }
  return Object.entries(tmp)
}

function getMoveTypeIconUrl(move) {
  let type = moveInfo[move].type
  return `/types/${type}.png`
}

function getMoveCategoryIconUrl(move) {
  let category = moveInfo[move].category
  return `/categories/${category}.png`
}

function getAccuracyText(accuracy) {
  if (accuracy === true) {
    return '100%';
  }
  return accuracy + '%';
}

function getSpreadText(spread, showNatureIndex, showNatureName) {
  let split = spread.split(':');
  let pokemonNature = split[0];
  let value = split[1].split('/')[showNatureIndex];

  if (nature[pokemonNature].plus === showNatureName) {
    value = value + '+';
  } else if (nature[pokemonNature].minus === showNatureName) {
    value = value + '-';
  }

  return value;
}

async function queryTeams(page, row, pokemon) {
  if (props.format !== 'gen9ou') {
    return
  }
  let url = new URL(`${apiUrl}/api/v2/teams?page=${page}&row=${row}&pokemons=${pokemon}&sort=maxRating&groupName=last_90_days`);

  const res = await fetch(url,
      {
        method: "GET"
      }
  );
  if (res.ok) {
    let result = await res.json();
    for ( let teamGroup of result.data) {
      teamGroup.teams = teamGroup.teams.slice(0, 1);
    }
    teams.value = result.data;
  }
}

async function queryPokemonSet(format, pokemon) {
  let url = new URL(`${apiUrl}/api/stats/${format}/set/` + pokemon);

  const res = await fetch(url,
      {
        method: "GET"
      }
  );
  if (res.ok) {
    let result = await res.json();
    sets.value = result.sets;
  }
}
</script>

<template>
  <div class="w-full" v-if="moveset">
    <div class="flex justify-start items-center mb-3">
      <img width="120" height="120"
           :src="`https://play.pokemonshowdown.com/sprites/dex/${pokemon.name.toLowerCase().replaceAll(' ','')}.png`"
           :alt="pokemon.name" :title="pokemon.name" @error="showDefaultIcon"/>
      <p class="text-3xl font-bold">{{ pokemon?.name }}</p>
    </div>
    <div class="flex justify-start items-center gap-2 mb-5">
      <Divider layout="vertical" type="solid"/>
      <div class="items-center">
        <p class="text-xl text-gray-500">weight</p>
        <div class="flex gap-5 w-44 items-center">
          <p class="text-xl font-bold">{{ convertToPercentage(pokemon.usage.weighted) }}</p>
          <UsageDif :newValue="pokemon.usage.weighted" :oldValue="pokemon.lastMonthUsage?.usage.weighted"/>
        </div>
      </div>
      <Divider layout="vertical" type="solid"/>
      <div class="items-center">
        <p class="text-xl text-gray-500">raw</p>
        <div class="flex gap-5 items-center">
          <p class="text-xl font-bold">{{ convertToPercentage(pokemon.usage.raw) }}</p>
          <UsageDif :newValue="pokemon.usage.raw" :oldValue="pokemon.lastMonthUsage?.usage.raw"/>
        </div>
      </div>
      <Divider layout="vertical"/>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">abilities</p>
      <div class="flex justify-start items-center gap-2 mb-1" v-for=" [ability, value] in Object.entries(moveset.abilities)">
        <span class="w-44">{{ ability }}</span>
        <div class="flex gap-5 w-44 items-center">
          <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
          <UsageDif :newValue="value" :oldValue="moveset.lastMonthMoveSet?.abilities[ability]"/>
        </div>
        <span class="">{{ abilityText[ability].shortDesc }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">items</p>
      <div class="flex justify-start gap-2 mb-1" v-for=" [item, value] in filterPopularSet(moveset.items,0.01)">
        <div class="w-44 items-center">
          <img :src="`/itemicon/${item}.png`" :alt="item"/>
          <span>{{ item }}</span>
        </div>
        <div class="flex gap-5 w-44 items-center">
          <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
          <UsageDif :newValue="value" :oldValue="moveset.lastMonthMoveSet?.items[item]"/>
        </div>
        <span>{{ itemText[item]?.desc }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">moves</p>
      <div class="flex justify-start items-center gap-2 mb-1" v-for=" [move, value] in filterPopularSet(moveset.moves,0.01)">
        <span class="w-44">{{ move }}</span>
        <div class="flex gap-5 w-44 items-center">
          <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
          <UsageDif :newValue="value" :oldValue="moveset.lastMonthMoveSet?.moves[move]"/>
        </div>
        <img :src="getMoveTypeIconUrl(move)" :alt="move"/>
        <img :src="getMoveCategoryIconUrl(move)" :alt="move"/>
        <span class="w-7">{{ moveInfo[move].basePower }}</span>
        <span class="w-12">{{ getAccuracyText(moveInfo[move].accuracy) }}</span>
        <span>{{ moveText[move].shortDesc }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">spreads</p>
      <div class="flex">
        <span class="w-32">HP</span>
        <span class="w-32">ATK</span>
        <span class="w-32">DEF</span>
        <span class="w-32">SPA</span>
        <span class="w-32">SPD</span>
        <span class="w-32">SPE</span>
      </div>
      <div class="flex justify-start items-center mb-1" v-for=" [spread, value] in
      filterPopularSet(moveset.spreads,0.025)">
        <span class="w-32">{{ getSpreadText(spread, 0, 'hp') }}</span>
        <span class="w-32">{{ getSpreadText(spread, 1, 'atk') }}</span>
        <span class="w-32">{{ getSpreadText(spread, 2, 'def') }}</span>
        <span class="w-32">{{ getSpreadText(spread, 3, 'spa') }}</span>
        <span class="w-32">{{ getSpreadText(spread, 4, 'spd') }}</span>
        <span class="w-32">{{ getSpreadText(spread, 5, 'spe') }}</span>
        <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">teammate</p>
      <div class="flex justify-start items-center gap-2 mb-1" v-for=" [teammate, value] in
      filterPopularSet(moveset.teammates,0.20)">
        <div class="w-60   ">
          <img :src="getIconUrl(teammate)" :alt="teammate"/>
          <span>{{ teammate }}</span>
        </div>
        <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
      </div>
    </div>
    <Divider type="solid" v-if="sets"/>
    <div class="ml-5 my-3" v-if="sets">
      <p class="text-xl text-gray-500">sets</p>
      <div class="mt-3 mb-10" v-for=" [setName, set] in Object.entries(sets)">
        <p class="font-bold">{{ setName }}</p>
        <pre >{{set}}</pre>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3" v-if="teams && teams.length !== 0">
      <p class="text-xl  mb-3">replay</p>
      <div class="mb-3 flex items-center text-center" v-for="teamGroup in teams">
        <Team class="w-1/3" :team="teamGroup" :compact="true" :teamSet="teamGroup.teamSet"></Team>
        <div class="flex gap-2 w-full" v-for="team in teamGroup.teams">
          <router-link :to="`/player-record?name=${team.playerName}`" class="text-black w-1/2">
            {{ team.playerName }}
          </router-link>
          <a :href="`https://replay.pokemonshowdown.com/${team.battleId}`" target="_blank" class="text-black w-1/2">
            {{ team.battleId }}
          </a>
        </div>
      </div>
    </div>
  </div>
  <ProgressSpinner v-else/>
</template>