<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref, watch} from "vue";

import Divider from 'primevue/divider';
import Textarea from 'primevue/textarea';
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';
import TreeSelect from 'primevue/treeselect';
import {Dex} from '@pkmn/dex';

import UsageDif from "@/components/stats/UsageDif.vue";
import {zh_translation_text} from "@/components/data/translationText.js"
import {nature} from "@/components/data/nature.js";
import Team from "@/components/Team.vue";
import Dialog from "primevue/dialog";
import TeamInfo from "@/components/TeamInfo.vue";
import LoadingIcon from "@/views/LoadingIcon.vue";

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
const teamInfoDialogVisible = ref(false);
const teamInfoId = ref();
const currentForm = ref(props.pokemon?.name);
let spreadStatMap = {};

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
  let tmp = {}
  for (let key in set) {
    if (set[key] >= thresold) {
      tmp[key] = set[key];
    }
  }
  return Object.entries(tmp)
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

function getBaseStat(spread, showNatureIndex, showNatureName, pokemon) {
  let split = spread.split(':');
  let pokemonNature = split[0];
  let value = split[1].split('/')[showNatureIndex];

  let isPlus = nature[pokemonNature].plus === showNatureName;
  let isMinus = nature[pokemonNature].minus === showNatureName;
  let key = `${pokemon}_${showNatureName}_${value}_${isPlus}_${isMinus}`;
  if (spreadStatMap[key] && spreadStatMap[key] !== spread) {
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

function getFormNodes(pokemon) {
  let formsNode = [];
  if (!pokemon) {
    return formsNode;
  }

  formsNode.push(
      {
        key: pokemon,
        label: getTranslation(pokemon),
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
          label: getTranslation(otherForm),
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

</script>

<template>
  <div class="w-full" v-if="moveset">
    <div class="flex justify-start items-center mb-3">
      <img width="120" height="120"
           :src="getPsIconUrl(currentForm)" :alt="pokemon.name" :title="pokemon.name" @error="showDefaultIcon"/>
      <div class="flex justify-start items-center">
        <p class="text-3xl font-bold mr-1 text-center items-center">{{ getTranslation(currentForm) }}</p>
        <img v-if="Dex.forGen(currentTierNumber).species.get(currentForm)"
             v-for="type in getPokemonTypes(currentForm)" :src="`/types/${type}.png`" height="17" width="40" :alt="type"/>
        <TreeSelect v-if="getFormNodes(pokemon?.name).length > 1" @node-select="onNodeSelect"
                    :options="getFormNodes(pokemon?.name)" class="max-w-12 ml-2"/>
        <div class="ml-4 w-56" v-if="Dex.forGen(currentTierNumber).species.get(currentForm)">
          <div v-for="(value, key) in getPokemonStats(currentForm)" class="flex gap-1 items-center text-center">
            <span class="font-mono text-sm w-6">{{ key }}</span>
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
        <span class="whitespace-nowrap">{{
            getTranslation(Dex.forGen(currentTierNumber).abilities.get(ability)?.shortDesc)
          }}</span>
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
        <span class="whitespace-nowrap">{{ getTranslation(Dex.forGen(currentTierNumber).items.get(item)?.desc) }}</span>
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
          <UsageDif :newValue="value" :oldValue="moveset.lastMonthMoveSet?.teraTypes?.[tera]"/>
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
        <span class="w-7 min-w-7 text-center">{{ getMoveBasePower(move) }}</span>
        <span class="w-12 min-w-12 text-center">{{ getAccuracyText(move) }}</span>
        <span class="w-7 min-w-7 text-center">{{ getMovePP(move) }}</span>
        <span class="whitespace-nowrap">{{
            getTranslation(Dex.forGen(currentTierNumber).moves.get(move)?.shortDesc)
          }}</span>
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
      filterPopularSet(moveset.spreads,0.01)">
        <span class="16 min-w-16">{{ getSpreadText(spread, 0, 'hp') }}</span>
        <span class="w-16 min-w-16 font-mono text-xs text-sky-500">{{
            getBaseStat(spread, 0, 'hp', currentForm)
          }}</span>

        <span class="w-16 min-w-16">{{ getSpreadText(spread, 1, 'atk') }}</span>
        <span class="w-16 min-w-16 font-mono text-xs text-sky-500">{{
            getBaseStat(spread, 1, 'atk', currentForm)
          }}</span>

        <span class="w-16 min-w-16">{{ getSpreadText(spread, 2, 'def') }}</span>
        <span class="w-16 min-w-16 font-mono text-xs text-sky-500">{{
            getBaseStat(spread, 2, 'def', currentForm)
          }}</span>

        <span class="w-16 min-w-16">{{ getSpreadText(spread, 3, 'spa') }}</span>
        <span class="w-16 min-w-16 font-mono text-xs text-sky-500">{{
            getBaseStat(spread, 3, 'spa', currentForm)
          }}</span>

        <span class="w-16 min-w-16">{{ getSpreadText(spread, 4, 'spd') }}</span>
        <span class="w-16 min-w-16 font-mono text-xs text-sky-500">{{
            getBaseStat(spread, 4, 'spd', currentForm)
          }}</span>

        <span class="w-16 min-w-16">{{ getSpreadText(spread, 5, 'spe') }}</span>
        <span class="w-16 min-w-16 font-mono text-xs text-sky-500">{{
            getBaseStat(spread, 5, 'spe', currentForm)
          }}</span>
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

        <Accordion :multiple="true" v-if="analysis?.setAnalyzes?.[setName]" class="mt-7">
          <AccordionTab>
            <template #header>
            <span class="flex align-items-center gap-2 w-full">
                <span class="font-bold white-space-nowrap">Smogon Analysis</span>
            </span>
            </template>
            <div class="">
              <Textarea v-model="analysis.setChineseAnalyzes[setName]" disabled rows="20" cols="60"
                        class="font-mono leading-loose rounded-3xl text-lg dynamicThemeText analysis-Bg"/>
              <Textarea v-model="analysis.setAnalyzes[setName]" disabled rows="20" cols="60"
                        class="font-mono leading-loose rounded-3xl bg-gray-50 text-lg dynamicThemeText analysis-Bg"/>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
    </div>
    <Divider type="solid"/>
    <div class="ml-5 my-3 whitespace-nowrap" v-if="teams && teams.length !== 0">
      <p class="text-xl  mb-3">teams</p>
      <div class="mb-3 flex items-center text-center" v-for="teamGroup in teams">
        <Team class="" :team="teamGroup" :compact="true" :teamSet="teamGroup.teamSet"></Team>
        <i class="ml-2 pi pi-eye cursor-pointer" style="font-size: 1rem"
           @click="toggleTeamInfoDialog(teamGroup.id.data)"/>
        <a class="ml-2" target="_blank" v-if="teamGroup.pokepasts?.length > 0" v-for="pokepast in teamGroup.pokepasts"
           :href="pokepast.url">
          <i class="pi pi-link" style="color: darkblue"></i>
        </a>
      </div>
    </div>
  </div>
  <span v-else-if="loadFail">load move set fail.</span>
  <LoadingIcon v-else/>
  <Dialog v-model:visible="teamInfoDialogVisible" modal header="Team Info" class="size-3/4">
    <div class="">
      <TeamInfo :teamId="teamInfoId"></TeamInfo>
    </div>
  </Dialog>
</template>

<style>
.analysis-Bg {
  background-color: var(--analysis-bg-color);
}
</style>