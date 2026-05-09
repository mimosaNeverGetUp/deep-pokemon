<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref, watch} from "vue";

import Divider from 'primevue/divider';
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import TreeSelect from 'primevue/treeselect';
import {Dex} from '@pkmn/dex';

import UsageDif from "@/components/stats/UsageDif.vue";
import {nature} from "@/components/data/nature.js";
import Team from "@/components/Team.vue";
import Dialog from "primevue/dialog";
import TeamInfo from "@/components/TeamInfo.vue";
import LoadingIcon from "@/views/LoadingIcon.vue";
import Button from "primevue/button";
import {useI18n} from 'vue-i18n'

const {locale, t} = useI18n()

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const props = defineProps({
  pokemon: Object,
  format: String,
  redirectPositionFunction: Function,
  language: {
    type: String,
    required: false,
    default: "en"
  }
});

// get current tier
const genRegex = /gen([0-9]+)/g;
let currentTier;
let currentTierNumber;
if (props.format.includes("1v1")) {
  currentTier = props.format.substring(0, props.format.indexOf("1v1"));
  currentTierNumber = currentTier.matchAll(genRegex).next().value[1];
} else if (props.format.includes("2v2")) {
  currentTier = props.format.substring(0, props.format.indexOf("2v2"));
  currentTierNumber = currentTier.matchAll(genRegex).next().value[1];
} else {
  currentTier = props.format.matchAll(genRegex).next().value[0];
  currentTierNumber = props.format.matchAll(genRegex).next().value[1];
}

const moveset = ref()
const sets = ref()
const analysis = ref()
const teams = ref(null)
const loadFail = ref(false)
const spreadsShowThreshold = ref(0.01)
const teamInfoDialogVisible = ref(false);
const teamInfoId = ref();
const teamTier = ref();
const currentForm = ref(props.pokemon?.name);
let spreadStatMap = {};
let hoverSpreadStatMap = ref({});

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
  event.target.src = getIconUrl(currentForm.value);
}

function getIconUrl(pokemon) {
  const iconName = pokemon.replace(" ", "").replace("-*", "")
  return "/pokemonicon/" + iconName + ".png"
}

function getPsIconUrl(pokemon) {
  return `https://play.pokemonshowdown.com/sprites/dex/${pokemon.toLowerCase().replaceAll(' ', '')}.png`;
}

watch(() => props.pokemon, async (newPokemon) => {
  moveset.value = null
  sets.value = null;
  analysis.value = null
  teams.value = null;
  currentForm.value = newPokemon.name;
  spreadsShowThreshold.value = 0.01;
  spreadStatMap = {};

  await fetchStatsData(props.format, newPokemon.name);
  props.redirectPositionFunction();
  await queryPokemonSet(props.format, newPokemon.name);
  await queryTeams(0, 5, newPokemon.name);
  await queryPokemonAnalysis(props.format, newPokemon.name);

});

function convertToPercentage(f) {
  return (f * 100).toFixed(2) + '%'
}

function filterPopularSet(set, thresold) {
  let tmp = []
  for (let key in set) {
    if (set[key] >= thresold) {
      let spread = {name: key, usage: set[key]};
      tmp.push(spread)
    }
  }
  return tmp;
}

function getMoveTypeIconUrl(move) {
  let type = Dex.forGen(currentTierNumber).moves.get(move)?.type;
  if (type === "???") {
    return "/types/null.png";
  }
  return `/types/${type}.png`;
}

function getMoveCategoryIconUrl(move) {
  let category = Dex.forGen(currentTierNumber).moves.get(move)?.category;
  return `/categories/${category}.png`
}

function getMoveBasePower(move) {
  return Dex.forGen(currentTierNumber).moves.get(move)?.basePower;
}

function getMovePP(move) {
  let pp = Dex.forGen(currentTierNumber).moves.get(move)?.pp;
  return pp ? pp * 1.6 : 'NA';
}

function getAccuracyText(move) {
  let accuracy = Dex.forGen(currentTierNumber).moves.get(move)?.accuracy;

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

  let isPlus = nature[pokemonNature].plus === showNatureName;
  let isMinus = nature[pokemonNature].minus === showNatureName;

  if (isPlus) {
    value = value + '+';
  } else if (isMinus) {
    value = value + '-';
  }
  return value;
}

function toggleSpreadText(spread, showNatureIndex, showNatureName, pokemon, index) {
  let split = spread.split(':');
  let pokemonNature = split[0];
  let value = split[1].split('/')[showNatureIndex];

  let isPlus = nature[pokemonNature].plus === showNatureName;
  let isMinus = nature[pokemonNature].minus === showNatureName;
  let key = `${pokemon}_${showNatureName}_${value}_${isPlus}_${isMinus}_${index}`;
  if (!hoverSpreadStatMap.value[key]) {
    hoverSpreadStatMap.value[key] = true;
  } else {
    hoverSpreadStatMap.value[key] = !hoverSpreadStatMap.value[key];
  }
}

function getBaseStat(spread, showNatureIndex, showNatureName, pokemon, index) {
  let split = spread.split(':');
  let pokemonNature = split[0];
  let value = split[1].split('/')[showNatureIndex];

  let isPlus = nature[pokemonNature].plus === showNatureName;
  let isMinus = nature[pokemonNature].minus === showNatureName;
  let key = `${pokemon}_${showNatureName}_${value}_${isPlus}_${isMinus}`;
  let hoverKey = `${pokemon}_${showNatureName}_${value}_${isPlus}_${isMinus}_${index}`;
  if (hoverSpreadStatMap.value[hoverKey]) {
    return getStat(pokemon, showNatureName, value, isMinus, isPlus);
  }

  if ((spreadStatMap[key] && spreadStatMap[key] !== spread)) {
    // not show repeat spread stat
  } else {
    spreadStatMap[key] = spread;
    return getStat(pokemon, showNatureName, value, isMinus, isPlus);
  }
}

function getStat(pokemon, natureName, spread, isMinus, isPlus) {
  let baseStrength = Dex.forGen(currentTierNumber).species.get(pokemon).baseStats[natureName];
  let baseStat;
  let level = 100;
  if (props.format.endsWith("lc")) {
    level = 5;
  } else if (props.format.includes("vgc")) {
    level = 50;
  }

  let maxIvInGen = currentTierNumber >= 3 ? 31 : 30;
  if (natureName === "hp") {
    baseStat = Math.floor(Math.floor(baseStrength * 2 + maxIvInGen + Math.floor(spread / 4)) * level / 100) + 10 + level;
  } else {
    baseStat = Math.floor(Math.floor(baseStrength * 2 + maxIvInGen + Math.floor(spread / 4)) * level / 100) + 5;
  }
  if (isMinus) {
    return Math.floor(baseStat * 0.9);
  } else if (isPlus) {
    return Math.floor(baseStat * 1.1);
  }
  return baseStat;
}

async function queryTeams(page, row, pokemon) {
  if (props.format !== 'gen9ou' && props.format !== 'gen9nationaldex') {
    return
  }
  let groupName = props.format === "gen9ou" ? "last_90_days" : "last_90_days_gen9nationaldex";
  let url = new URL(`${apiUrl}/api/v2/teams?page=${page}&row=${row}&pokemons=${pokemon}&sort=maxRating&groupName=${groupName}`);

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
      sets.value = result;
    } catch (e) {
      console.log("response is empty or invalid")
    }
  }
}

async function queryPokemonAnalysis(format, pokemon) {
  let url = new URL(`${apiUrl}/api/stats/${format}/analysis/` + pokemon);

  const res = await fetch(url,
      {
        method: "GET"
      }
  );
  if (res.ok) {
    try {
      let result = await res.json();
      analysis.value = result;
    } catch (e) {
      console.log("response is empty or invalid")
    }
  }
}

function getPokemonTypes(name) {
  return Dex.forGen(currentTierNumber).species.get(name)?.types;
}

function getPokemonStats(name) {
  return Dex.forGen(currentTierNumber).species.get(name)?.baseStats;
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

function toggleTeamInfoDialog(teamId, tier) {
  teamInfoId.value = teamId;
  teamTier.value = tier;
  teamInfoDialogVisible.value = true;
}

function getFormNodes(pokemon) {
  let formsNode = [];
  if (!pokemon) {
    return formsNode;
  }

  formsNode.push(
      {
        key: pokemon,
        label: t(pokemon),
        children: []
      }
  );

  let otherFormes = Dex.forGen(currentTierNumber).species.get(pokemon)?.otherFormes;
  if (otherFormes) {
    for (const otherForm of otherFormes) {
      let formInfo = Dex.forGen(currentTierNumber).species.get(otherForm);

      // only filter form in battle exclude mega or item form
      if (formInfo.gen <= currentTierNumber && formInfo.battleOnly && !formInfo.requiredItem) {
        formsNode.push({
          key: otherForm,
          label: t(otherForm),
          children: []
        });
      }
    }
  }

  return formsNode;
}

function onNodeSelect(event) {
  currentForm.value = event.key;
}

function allSpreadsButtonIcon() {
  return spreadsShowThreshold.value === 0 ? "pi pi-angle-up" : "pi pi-angle-down";
}

function toggleSpreadsVisibility() {
  if (spreadsShowThreshold.value > 0.002) {
    spreadsShowThreshold.value = 0.002;
  } else if (spreadsShowThreshold.value > 0.0005) {
    spreadsShowThreshold.value = 0.0005;
  } else if (spreadsShowThreshold.value > 0) {
    spreadsShowThreshold.value = 0;
  } else {
    spreadsShowThreshold.value = 0.01;
  }
}

function getShowSpreads(spreads) {
  return filterPopularSet(spreads, spreadsShowThreshold.value);
}

function getSpreadTextClass() {
  return "w-16 min-w-16 max-lg:w-1/7 max-lg:min-w-1/7";
}

function getSpreadValueTextClass() {
  return "w-16 min-w-16 font-mono text-xs text-sky-500 max-lg:w-1/7 max-lg:min-w-1/7";
}

function getSpreadDivClass() {
  return "flex justify-start items-center w-32 min-w-32 max-lg:w-1/7 max-lg:min-w-1/7 max-lg:flex-col max-lg:items-start max-lg:mb-4 max-lg:min-h-10";
}

function getLocaleSets(set) {
  if (locale.value === "zh" && set.chineseSets) {
    return Object.entries(set.chineseSets);
  }
  return Object.entries(set.sets);
}
</script>

<template>
  <div class="w-full" v-if="moveset">
    <div class="flex justify-start items-center mb-3 max-sm:flex-col max-sm:items-start max-sm:gap-2">
      <div class="flex justify-start items-center">
        <img width="120" height="120"
             :src="getPsIconUrl(currentForm)" :alt="pokemon.name" :title="pokemon.name" @error="showDefaultIcon"/>
        <p class="text-2xl font-bold mr-1 text-center items-center">{{ $t(currentForm) }}</p>
        <img v-if="Dex.forGen(currentTierNumber).species.get(currentForm)"
             v-for="type in getPokemonTypes(currentForm)" :src="`/types/${type}.png`" height="17" width="40"
             :alt="type"/>
        <TreeSelect v-if="getFormNodes(pokemon?.name).length > 1" @node-select="onNodeSelect"
                    :options="getFormNodes(pokemon?.name)" class="max-w-12 ml-2"/>
      </div>
      <div class="ml-4 w-56" v-if="Dex.forGen(currentTierNumber).species.get(currentForm)">
        <div v-for="(value, key) in getPokemonStats(currentForm)" class="flex gap-1 items-center text-center">
          <span class="text-gray-500 text-xs w-8">{{ $t(key) }}</span>
          <span :style="getStatStyle(key,value)" class="size-3.5"></span>
          <span class="text-sm">{{ value }}</span>
        </div>
      </div>
    </div>
    <div class="flex justify-start items-center gap-2 mb-5 max-sm:gap-1">
      <Divider layout="vertical" type="solid"/>
      <div class="ml-3 items-center max-sm:ml-1">
        <p class="text-sm text-gray-500">{{ $t("weighted") }}</p>
        <div class="flex gap-5 w-44 min-w-44 items-center max-sm:w-40 max-sm:min-w-40">
          <p class="text-xl font-bold">{{ convertToPercentage(pokemon.usage.weighted) }}</p>
          <UsageDif :newValue="pokemon.usage.weighted" :oldValue="pokemon.lastMonthUsage?.usage.weighted"/>
        </div>
      </div>
      <Divider layout="vertical" type="solid"/>
      <div v-if="pokemon.usage.raw !== 0" class="items-center max-sm:ml-1">
        <p class="text-sm text-gray-500">{{ $t("raw") }}</p>
        <div class="flex gap-5 w-44 min-w-44 items-center max-sm:w-40 max-sm:min-w-40">
          <p class="text-xl font-bold">{{ convertToPercentage(pokemon.usage.raw) }}</p>
          <UsageDif :newValue="pokemon.usage.raw" :oldValue="pokemon.lastMonthUsage?.usage.raw"/>
        </div>
      </div>
      <Divider v-if="pokemon.usage.raw !== 0" layout="vertical"/>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3 max-sm:ml-1">
      <p class="text-sm text-gray-500">{{ $t("abilities") }}</p>
      <div class="flex justify-start items-center max-lg:flex-col max-lg:items-start max-lg:mb-1"
           v-for=" [ability, value] in Object.entries(moveset.abilities)">
        <div class="flex justify-start items-center gap-2 mb-1 max-sm:gap-1">
          <span class="w-44 min-w-44 max-sm:w-36 max-sm:min-w-36 max-sm:text-base">{{ $t(ability) }}</span>
          <div class="flex gap-5 w-44 min-w-44 items-center">
            <span class="font-bold w-20 max-sm:text-base">{{ convertToPercentage(value) }}</span>
            <UsageDif :newValue="value" :oldValue="moveset.lastMonthMoveSet?.abilities[ability]"/>
          </div>
        </div>
        <p class="text-gray-500 text-sm max-sm:text-xs max-lg:break-all max-lg:ml-6 lg:whitespace-nowrap">{{
            $t(Dex.forGen(currentTierNumber).abilities.get(ability)?.shortDesc)
          }}</p>
      </div>

    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3 max-sm:ml-1">
      <p class="text-sm text-gray-500">{{ $t("items") }}</p>
      <div class="flex justify-start items-center max-lg:flex-col max-lg:items-start max-lg:mb-1"
           v-for=" (item) in filterPopularSet(moveset.items,0.01)">
        <div class="flex justify-start gap-2 mb-1 max-sm:gap-1">
          <div class="w-44 items-center min-w-44 max-sm:w-36 max-sm:min-w-36 max-sm:text-base">
            <img :src="`/itemicon/${item.name}.png`" :alt="item.name"/>
            <span>{{ $t(item.name) }}</span>
          </div>
          <div class="flex gap-5 w-44 min-w-44 items-center">
            <span class="font-bold w-20 max-sm:text-base">{{ convertToPercentage(item.usage) }}</span>
            <UsageDif :newValue="item.usage" :oldValue="moveset.lastMonthMoveSet?.items[item.name]"/>
          </div>
        </div>
        <p class="text-gray-500 text-sm max-sm:text-xs max-lg:break-all max-lg:ml-6 lg:whitespace-nowrap">{{
            $t(Dex.forGen(currentTierNumber).items.get(item.name)?.desc)
          }}</p>
      </div>
    </div>
    <Divider type="solid"/>
    <div v-if="props.format.includes('gen9') && moveset.teraTypes" class="ml-5 my-3 max-sm:ml-1">
      <p class="text-sm text-gray-500">{{ $t("tera types") }}</p>
      <div class="flex justify-start gap-2 mb-1 max-sm:gap-1" v-for=" (tera) in filterPopularSet(moveset.teraTypes,0.01)">
        <div class="w-44 items-center min-w-44 max-sm:w-36 max-sm:min-w-36 max-sm:text-base">
          <img :src="`/types/${tera.name}.png`" :alt="tera.name"/>
          <span>{{ $t(tera.name) }}</span>
        </div>
        <div class="flex gap-5 w-44 min-w-44 items-center">
          <span class="font-bold w-20">{{ convertToPercentage(tera.usage) }}</span>
          <UsageDif :newValue="tera.usage" :oldValue="moveset.lastMonthMoveSet?.teraTypes?.[tera.name]"/>
        </div>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3 max-sm:ml-1">
      <p class="text-sm text-gray-500">{{ $t("moves") }}</p>
      <div class="flex justify-start items-center max-lg:flex-col max-lg:items-start max-lg:mb-1"
           v-for=" (move) in filterPopularSet(moveset.moves,0.01)">
        <div class="flex justify-start items-center gap-2 mb-1">
          <span class="w-44 min-w-44 max-sm:w-36 max-sm:min-w-3 max-sm:text-base">{{ $t(move.name) }}</span>
          <div class="flex gap-5 w-44 min-w-44 items-center">
            <span class="font-bold w-20 max-sm:text-base">{{ convertToPercentage(move.usage) }}</span>
            <UsageDif :newValue="move.usage" :oldValue="moveset.lastMonthMoveSet?.moves[move.name]"/>
          </div>
          <img :src="getMoveTypeIconUrl(move.name)" :alt="move" class="max-lg:hidden"/>
          <img :src="getMoveCategoryIconUrl(move.name)" :alt="move" class="max-lg:hidden"/>
          <span class="w-7 min-w-7 text-center text-gray-500 text-sm max-lg:hidden">{{
              getMoveBasePower(move.name)
            }}</span>
          <span class="w-12 min-w-12 text-center text-gray-500 text-sm max-lg:hidden">{{
              getAccuracyText(move.name)
            }}</span>
          <span class="w-7 min-w-7 text-center text-gray-500 text-sm max-lg:hidden">{{ getMovePP(move.name) }}</span>
        </div>
        <p class="text-gray-500 text-sm  max-sm:text-xs max-lg:break-all max-lg:ml-6 lg:whitespace-nowrap">{{
            $t(Dex.forGen(currentTierNumber).moves.get(move.name)?.shortDesc)
          }}
        </p>
      </div>

    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3 max-sm:ml-1">
      <p class="text-sm text-gray-500 max-sm:text-xs">{{ $t("spreads") }}</p>
      <div class="flex">
        <span class="w-32 min-w-32 text-sm text-gray-500 max-lg:w-1/7 max-lg:min-w-1/7 max-sm:text-xs">{{
            $t('Hp')
          }}</span>
        <span class="w-32 min-w-32 text-sm text-gray-500 max-lg:w-1/7 max-lg:min-w-1/7 max-sm:text-xs">{{
            $t('Atk')
          }}</span>
        <span class="w-32 min-w-32 text-sm text-gray-500 max-lg:w-1/7 max-lg:min-w-1/7 max-sm:text-xs">{{
            $t('Def')
          }}</span>
        <span class="w-32 min-w-32 text-sm text-gray-500 max-lg:w-1/7 max-lg:min-w-1/7 max-sm:text-xs">{{
            $t('SpA')
          }}</span>
        <span class="w-32 min-w-32 text-sm text-gray-500 max-lg:w-1/7 max-lg:min-w-1/7 max-sm:text-xs">{{
            $t('SpD')
          }}</span>
        <span class="w-32 min-w-32 text-sm text-gray-500 max-lg:w-1/7 max-lg:min-w-1/7 max-sm:text-xs">{{
            $t('Spe')
          }}</span>
      </div>
      <div class="flex justify-start items-center mb-1 max-sm:text-sm"
           v-for="(spread, index) in getShowSpreads(moveset.spreads)">
        <div :class="getSpreadDivClass()">
          <p :class="getSpreadTextClass()" @mouseover="toggleSpreadText(spread.name, 0, 'hp', currentForm, index)"
             @mouseleave="toggleSpreadText(spread.name, 0, 'hp', currentForm, index)">
            {{ getSpreadText(spread.name, 0, 'hp') }}
          </p>
          <p :class="getSpreadValueTextClass()"
             @mouseover="toggleSpreadText(spread.name, 0, 'hp', currentForm, index)"
             @mouseleave="toggleSpreadText(spread.name, 0, 'hp', currentForm, index)">
            {{ getBaseStat(spread.name, 0, 'hp', currentForm, index) }}
          </p>
        </div>

        <div :class="getSpreadDivClass()">
          <p :class="getSpreadTextClass()" @mouseover="toggleSpreadText(spread.name, 1, 'atk',currentForm, index)"
                @mouseleave="toggleSpreadText(spread.name, 1, 'atk', currentForm, index)">
            {{ getSpreadText(spread.name, 1, 'atk') }}
          </p>
          <span :class="getSpreadValueTextClass()"
                @mouseover="toggleSpreadText(spread.name, 1, 'atk', currentForm, index)"
                @mouseleave="toggleSpreadText(spread.name, 1, 'atk', currentForm, index)">
            {{ getBaseStat(spread.name, 1, 'atk', currentForm, index) }}
          </span>
        </div>


        <div :class="getSpreadDivClass()">
          <p :class="getSpreadTextClass()" @mouseover="toggleSpreadText(spread.name, 2, 'def',
        currentForm, index)" @mouseleave="toggleSpreadText(spread.name, 2, 'def', currentForm, index)">
          {{ getSpreadText(spread.name, 2, 'def') }}
          </p>
          <p :class="getSpreadValueTextClass()"
                @mouseover="toggleSpreadText(spread.name, 2, 'def', currentForm, index)"
                @mouseleave="toggleSpreadText(spread.name, 2, 'def', currentForm, index)">{{
              getBaseStat(spread.name, 2, 'def', currentForm, index)
            }}
          </p>
        </div>

        <div :class="getSpreadDivClass()">
          <p :class="getSpreadTextClass()" @mouseover="toggleSpreadText(spread.name, 3, 'spa', currentForm, index)"
              @mouseleave="toggleSpreadText(spread.name, 3, 'spa', currentForm, index)">
            {{ getSpreadText(spread.name, 3, 'spa') }}
          </p>
          <p :class="getSpreadValueTextClass()"
                @mouseover="toggleSpreadText(spread.name, 3, 'spa', currentForm, index)"
                @mouseleave="toggleSpreadText(spread.name, 3, 'spa', currentForm, index)">
            {{getBaseStat(spread.name, 3, 'spa', currentForm, index)}}
          </p>
        </div>

        <div :class="getSpreadDivClass()">
            <p :class="getSpreadTextClass()" @mouseover="toggleSpreadText(spread.name, 4, 'spd', currentForm, index)"
                  @mouseleave="toggleSpreadText(spread.name, 4, 'spd', currentForm, index)">
              {{ getSpreadText(spread.name, 4, 'spd') }}
            </p>
            <p :class="getSpreadValueTextClass()"
                @mouseover="toggleSpreadText(spread.name, 4, 'spd', currentForm, index)"
                @mouseleave="toggleSpreadText(spread.name, 4, 'spd', currentForm, index)">
              {{getBaseStat(spread.name, 4, 'spd', currentForm, index) }}
            </p>
        </div>

        <div :class="getSpreadDivClass()">
            <span :class="getSpreadTextClass()" @mouseover="toggleSpreadText(spread.name,  5, 'spe', currentForm, index)"
                  @mouseleave="toggleSpreadText(spread.name,  5, 'spe', currentForm, index)">
              {{ getSpreadText(spread.name, 5, 'spe') }}
            </span>
          <span :class="getSpreadValueTextClass()"
                @mouseover="toggleSpreadText(spread.name,  5, 'spe', currentForm, index)"
                @mouseleave="toggleSpreadText(spread.name,  5, 'spe', currentForm, index)">
              {{getBaseStat(spread.name, 5, 'spe', currentForm, index)}}
          </span>
        </div>
        <span class="w-20 font-bold max-lg:w-1/7 max-lg:min-w-1/7 max-lg:min-h-10 max-lg:mb-4">{{ convertToPercentage(spread.usage) }}</span>
      </div>
      <Button class="ml-[384px] max-lg:hidden" :icon="allSpreadsButtonIcon()" severity="secondary"
              @click="toggleSpreadsVisibility()" rounded text/>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3">
      <p class="text-sm text-gray-500">{{ $t("teammate") }}</p>
      <div class="flex justify-start items-center gap-2 mb-1 max-sm:text-base" v-for=" (teammate) in
      filterPopularSet(moveset.teammates,0.20)">
        <div class="w-60   ">
          <img :src="getIconUrl(teammate.name)" :alt="teammate.name"/>
          <span>{{ $t(teammate.name) }}</span>
        </div>
        <span class="font-bold w-20">{{ convertToPercentage(teammate.usage) }}</span>
      </div>
    </div>
    <Divider type="solid" v-if="sets"/>
    <div class="ml-5 my-3 w-full lg:min-w-[800px]" v-if="sets">
      <p class="text-sm text-gray-500">{{ $t("sets") }}</p>
      <div class="mt-3 mb-10" v-for=" [setName, set] in getLocaleSets(sets)">
        <p class="font-bold">{{ setName }}</p>
        <pre class="max-sm:text-base max-sm:whitespace-pre-line">{{ set }}</pre>

        <Accordion :multiple="true" v-if="analysis?.setAnalyzes?.[setName]" class="mt-7">
          <AccordionTab>
            <template #header>
              <div class="flex align-items-center gap-2 w-full">
                <p class="font-bold white-space-nowrap">{{ $t("Smogon Analysis") }}</p>
              </div>
              <i class="pi pi-chevron-down"></i>
            </template>
            <div class="">
              <p class="font-sans text-base leading-loose dynamicThemeText  whitespace-pre-line max-w-200">
                {{ locale === 'en' ? analysis.setAnalyzes[setName] : analysis.setChineseAnalyzes[setName] }}
              </p>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3 whitespace-nowrap  max-sm:ml-0" v-if="teams && teams.length !== 0">
      <p class="text-sm mb-3">{{ $t("teams") }}</p>
      <div class="mb-3 flex items-center text-center" v-for="teamGroup in teams">
        <Team :team="teamGroup" :compact="false" :teamSet="teamGroup.teamSet"></Team>
        <i class="ml-2 pi pi-eye cursor-pointer" style="font-size: 1rem"
           @click="toggleTeamInfoDialog(teamGroup.id.data, teamGroup.tier)"/>
        <a class="ml-2 max-sm:hidden" target="_blank" v-if="teamGroup.pokepasts?.length > 0"
           v-for="pokepast in teamGroup.pokepasts" :href="pokepast.url">
          <i class="pi pi-link text-blue-400"></i>
        </a>
      </div>
    </div>
  </div>
  <span v-else-if="loadFail">load move set fail.</span>
  <LoadingIcon v-else/>
  <Dialog v-model:visible="teamInfoDialogVisible" modal :header="$t('Team Info')" class="">
    <div class="">
      <TeamInfo :teamId="teamInfoId" :teamTier="teamTier"></TeamInfo>
    </div>
  </Dialog>
</template>

<style>
.analysis-Bg {
  background-color: var(--analysis-bg-color);
}
</style>