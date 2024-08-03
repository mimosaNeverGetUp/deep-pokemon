<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref, watch} from "vue";

import Divider from 'primevue/divider';
import ProgressSpinner from 'primevue/progressspinner';
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
  await queryTeams(0, 7, newPokemon.name);
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
  let url = new URL(`${apiUrl}/api/teams?page=${page}&row=${row}&pokemons=${pokemon}`);

  const res = await fetch(url,
      {
        method: "GET"
      }
  );
  if (res.ok) {
    let result = await res.json();
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
      <div class="w-1/6 text-center	">
        <p class="text-xl text-gray-500">weight</p>
        <p class="text-xl font-bold">{{ convertToPercentage(pokemon.usage.weighted) }}</p>
      </div>
      <Divider layout="vertical" type="solid"/>
      <div class="w-1/6 text-center	">
        <p class="text-xl text-gray-500">raw</p>
        <p class="text-xl font-bold">{{ convertToPercentage(pokemon.usage.raw) }}</p>
      </div>
      <Divider layout="vertical"/>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">abilities</p>
      <div class="flex justify-start gap-2 mb-1" v-for=" [ability, value] in Object.entries(moveset.abilities)">
        <span class="w-44">{{ ability }}</span>
        <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
        <span class="">{{ abilityText[ability].shortDesc }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">items</p>
      <div class="flex justify-start gap-2 mb-1" v-for=" [item, value] in filterPopularSet(moveset.items,0.01)">
        <div class="w-44">
          <img :src="`/itemicon/${item}.png`" :alt="item"/>
          <span>{{ item }}</span>
        </div>
        <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
        <span>{{ itemText[item].desc }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">moves</p>
      <div class="flex justify-start items-center gap-2 mb-1" v-for=" [move, value] in
      filterPopularSet(moveset.moves,0.01)">
        <span class="w-44">{{ move }}</span>
        <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
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
    <div class="ml-5 my-3" v-if="teams">
      <p class="text-xl text-gray-500">replay</p>
      <div class="mb-1 flex text-center" v-for="team in teams">
        <Team class="w-1/3" :team="team" :compact="true"></Team>
        <span class="w-40">{{ team.playerName }}</span>
        <a class="text-green-300 w-1/3" style="display:block" target="_blank"
           :href="`https://replay.pokemonshowdown.com/${team.battleId}`">
          {{ team.battleId }}
        </a>
      </div>
    </div>
  </div>
  <ProgressSpinner v-else/>
</template>