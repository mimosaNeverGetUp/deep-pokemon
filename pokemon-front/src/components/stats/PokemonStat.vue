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
import {pokemoninfo} from "@/components/data/pokemoninfo.js"
import {zh_translation_text} from "@/components/data/translationText.js"
import {moveInfo} from "@/components/data/moveInfo.js";
import {nature} from "@/components/data/nature.js";
import Team from "@/components/Team.vue";
import Dialog from "primevue/dialog";
import TeamInfo from "@/components/TeamInfo.vue";

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const props = defineProps({
  pokemon: Object,
  format: String,
  language: {
    type: String,
    required: false,
    default: "en"
  }
})

const moveset = ref()
const sets = ref()
const teams = ref(null)
const loadFail = ref(false)
const teamInfoDialogVisible = ref(false);
const teamInfoId = ref();

async function fetchStatsData(format, pokemon) {
  const res = await fetch(`${apiUrl}/api/stats/${format}/moveset/` + pokemon, {
        method: "GET"
      }
  );
  if (res.ok) {
    moveset.value = await res.json();
    loadFail.value = false;
  } else {
    loadFail.value = true;
  }
}

function showDefaultIcon(event) {
  event.target.src = getIconUrl(props.pokemon.name);
}

function getIconUrl(pokemon) {
  const iconName = pokemon.replace(" ", "").replace("-*", "")
  return "/pokemonicon/" + iconName + ".png"
}

watch(() => props.pokemon, async (newPokemon) => {
  moveset.value = null
  sets.value = null;
  teams.value = null;

  await fetchStatsData(props.format, newPokemon.name);
  window.scrollTo({
    top: 0,
    behavior: 'auto'
  });
  await queryPokemonSet(props.format, newPokemon.name);
  await queryTeams(0, 5, newPokemon.name);
});

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
  let type = moveInfo[move]?.type
  return `/types/${type}.png`
}

function getMoveCategoryIconUrl(move) {
  let category = moveInfo[move]?.category
  return `/categories/${category}.png`
}

function getAccuracyText(accuracy) {
  if (accuracy === true) {
    return '100%';
  }
  if (!accuracy) {
    return "NA"
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
    for (let teamGroup of result.data) {
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
    try {
      let result = await res.json();
      sets.value = result.sets;
    } catch (e) {
      console.log("response is empty or invalid")
    }
  }
}

function getPokemonTypes(name) {
  return pokemoninfo[name]?.types;
}

function getPokemonStats(name) {
  return pokemoninfo[name]?.baseStats;
}

function getStatStyle(stat, value) {
  let width = 180 * value / 255;
  let bg;
  switch (stat) {
    case "hp":
      bg = "rgb(85, 137, 54)";
      break;
    case "atk":
      bg = "rgb(248, 203, 60)";
      break;
    case "def":
      bg = "rgb(217, 136, 55)";
      break;
    case "spa":
      bg = "rgb(89, 195, 208)";
      break;
    case "spd":
      bg = "rgb(88, 144, 205)";
      break;
    case "spe":
      bg = "rgb(164, 86, 208)";
      break;
  }

  return `width:${width}px;background:${bg}`
}

function toggleTeamInfoDialog(teamId) {
  teamInfoDialogVisible.value = true;
  teamInfoId.value = teamId;
}

function getTranslation(text) {
  if (props.language === "zh" && zh_translation_text[text]) {
    return zh_translation_text[text];
  }

  return text;
}

</script>

<template>
  <div class="w-full" v-if="moveset">
    <div class="flex justify-start items-center mb-3">
      <img width="120" height="120"
           :src="`https://play.pokemonshowdown.com/sprites/dex/${pokemon.name.toLowerCase().replaceAll(' ','')}.png`"
           :alt="pokemon.name" :title="pokemon.name" @error="showDefaultIcon"/>
      <div class="flex justify-start items-center">
        <p class="text-3xl font-bold mr-1 text-center items-center">{{ getTranslation(pokemon?.name) }}</p>
        <img v-if="pokemoninfo[pokemon?.name]" v-for="type in getPokemonTypes(pokemon?.name)"
             :src="`/types/${type}.png`" height="17" width="40" :alt="type"/>
        <div class="ml-4 w-56" v-if="pokemoninfo[pokemon?.name]">
          <div v-for="(value, key) in getPokemonStats(pokemon?.name)" class="flex gap-1 items-center text-center">
            <span class="font-mono  text-sm w-6">{{ key }}</span>
            <span :style="getStatStyle(key,value)" class="size-3.5"></span>
            <span class="text-sm">{{ value }}</span>
          </div>
        </div>
      </div>

    </div>
    <div class="flex justify-start items-center gap-2 mb-5">
      <Divider layout="vertical" type="solid"/>
      <div class="ml-3 items-center">
        <p class="text-xl text-gray-500">{{ getTranslation("weight") }}</p>
        <div class="flex gap-5 w-44 min-w-44 items-center">
          <p class="text-xl font-bold">{{ convertToPercentage(pokemon.usage.weighted) }}</p>
          <UsageDif :newValue="pokemon.usage.weighted" :oldValue="pokemon.lastMonthUsage?.usage.weighted"/>
        </div>
      </div>
      <Divider layout="vertical" type="solid"/>
      <div class="items-center">
        <p class="text-xl text-gray-500">{{ getTranslation("raw") }}</p>
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
      <div class="flex justify-start items-center gap-2 mb-1"
           v-for=" [ability, value] in Object.entries(moveset.abilities)">
        <span class="w-44 min-w-44">{{ getTranslation(ability) }}</span>
        <div class="flex gap-5 w-44 min-w-44 items-center">
          <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
          <UsageDif :newValue="value" :oldValue="moveset.lastMonthMoveSet?.abilities[ability]"/>
        </div>
        <span class="whitespace-nowrap">{{ getTranslation(abilityText[ability]?.shortDesc) }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">items</p>
      <div class="flex justify-start gap-2 mb-1" v-for=" [item, value] in filterPopularSet(moveset.items,0.01)">
        <div class="w-44 items-center min-w-44">
          <img :src="`/itemicon/${item}.png`" :alt="item"/>
          <span>{{ getTranslation(item) }}</span>
        </div>
        <div class="flex gap-5 w-44 min-w-44 items-center">
          <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
          <UsageDif :newValue="value" :oldValue="moveset.lastMonthMoveSet?.items[item]"/>
        </div>
        <span class="whitespace-nowrap">{{ getTranslation(itemText[item]?.desc) }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div v-if="props.format.includes('gen9') && moveset.teraTypes" class="ml-5 my-3">
      <p class="text-xl text-gray-500">tera types</p>
      <div class="flex justify-start gap-2 mb-1" v-for=" [tera, value] in filterPopularSet(moveset.teraTypes,0.01)">
        <div class="w-44 items-center min-w-44">
          <img :src="`/types/${tera}.png`" :alt="tera"/>
          <span>{{ getTranslation(tera) }}</span>
        </div>
        <div class="flex gap-5 w-44 min-w-44 items-center">
          <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
        </div>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">moves</p>
      <div class="flex justify-start items-center gap-2 mb-1"
           v-for=" [move, value] in filterPopularSet(moveset.moves,0.01)">
        <span class="w-44 min-w-44">{{ getTranslation(move) }}</span>
        <div class="flex gap-5 w-44 min-w-44 items-center">
          <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
          <UsageDif :newValue="value" :oldValue="moveset.lastMonthMoveSet?.moves[move]"/>
        </div>
        <img :src="getMoveTypeIconUrl(move)" :alt="move"/>
        <img :src="getMoveCategoryIconUrl(move)" :alt="move"/>
        <span class="w-7 text-center">{{ moveInfo[move]?.basePower }}</span>
        <span class="w-12 text-center">{{ getAccuracyText(moveInfo[move]?.accuracy) }}</span>
        <span class="whitespace-nowrap">{{ getTranslation(moveText[move]?.shortDesc) }}</span>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-xl text-gray-500">spreads</p>
      <div class="flex">
        <span class="w-32 min-w-32">{{ getTranslation('Hp') }}</span>
        <span class="w-32 min-w-32">{{ getTranslation('Atk') }}</span>
        <span class="w-32 min-w-32">{{ getTranslation('Def') }}</span>
        <span class="w-32 min-w-32">{{ getTranslation('SpA') }}</span>
        <span class="w-32 min-w-32">{{ getTranslation('SpD') }}</span>
        <span class="w-32 min-w-32">{{ getTranslation('Spe') }}</span>
      </div>
      <div class="flex justify-start items-center mb-1" v-for=" [spread, value] in
      filterPopularSet(moveset.spreads,0.025)">
        <span class="w-32 min-w-32">{{ getSpreadText(spread, 0, 'hp') }}</span>
        <span class="w-32 min-w-32">{{ getSpreadText(spread, 1, 'atk') }}</span>
        <span class="w-32 min-w-32">{{ getSpreadText(spread, 2, 'def') }}</span>
        <span class="w-32 min-w-32">{{ getSpreadText(spread, 3, 'spa') }}</span>
        <span class="w-32 min-w-32">{{ getSpreadText(spread, 4, 'spd') }}</span>
        <span class="w-32 min-w-32">{{ getSpreadText(spread, 5, 'spe') }}</span>
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
          <span>{{ getTranslation(teammate) }}</span>
        </div>
        <span class="font-bold w-20">{{ convertToPercentage(value) }}</span>
      </div>
    </div>
    <Divider type="solid" v-if="sets"/>
    <div class="ml-5 my-3" v-if="sets">
      <p class="text-xl text-gray-500">sets</p>
      <div class="mt-3 mb-10" v-for=" [setName, set] in Object.entries(sets)">
        <p class="font-bold">{{ setName }}</p>
        <pre>{{ set }}</pre>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3 whitespace-nowrap" v-if="teams && teams.length !== 0">
      <p class="text-xl  mb-3">teams</p>
      <div class="mb-3 flex items-center text-center" v-for="teamGroup in teams">
        <Team class="" :team="teamGroup" :compact="true" :teamSet="teamGroup.teamSet"></Team>
        <i class="ml-2 pi pi-eye cursor-pointer" style="font-size: 1rem" @click="toggleTeamInfoDialog(teamGroup.id.data)"/>
      </div>
    </div>
  </div>
  <span v-else-if="loadFail">load move set fail.</span>
  <ProgressSpinner v-else/>
  <Dialog v-model:visible="teamInfoDialogVisible" modal header="Team Info" class="size-3/4">
    <div class="">
      <TeamInfo :teamId="teamInfoId"></TeamInfo>
    </div>
  </Dialog>
</template>