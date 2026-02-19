<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import InputText from 'primevue/inputtext';
import {ref} from "vue";
import Button from 'primevue/button';
import {Dex} from '@pkmn/dex';
import TeamInfo from "@/components/TeamInfo.vue";
import TreeSelect from "primevue/treeselect";
import {zh_ps} from '@/locales/zh/zh_ps.js'

const apiUrl = import.meta.env.VITE_BACKEND_URL;

const input = ref();
const load = ref()
const teamInfoId = ref();
const teamTier = ref("gen9ou");
const teamTierNodes = [
  {
    key: "gen9ou",
    label: "gen9ou",
    children: []
  },
  {
    key: "gen8ou",
    label: "gen8ou",
    children: []
  },
  {
    key: "gen9nationaldex",
    label: "gen9nationaldex",
    children: []
  }
];
const zh_reverse_en_locales = buildReverseIndexMap(zh_ps);

async function queryTeam() {
  if (input.value) {
    teamInfoId.value = null;
    teamInfoId.value = getTeamId(input.value.split("/"));
  }
}

function getTeamId(team) {
  let numbers = [];
  for (let pokemon of team) {
    let name = pokemon.trim();

    if (hasChinese(name) && zh_reverse_en_locales[name]) {
      name = zh_reverse_en_locales[name]
    }
    if (name.includes("-*")) {
      name = name.substring(0, name.lastIndexOf('-*'));
    }
    let species = Dex.forGen(9).species.get(name);
    if (species) {
      numbers.push(species.num);
    }
  }
  numbers.sort((a, b) => a - b);

  let teamId = ""
  for (let number of numbers) {
    teamId = teamId + String(number).padStart(4, "0");
  }
  return btoa(teamId);
}

function onNodeSelect(event) {
  teamTier.value = event.key;
}

function buildReverseIndexMap(map) {
  const reverseMap = {};
  for (const key in map) {
    reverseMap[map[key]] = key;
  }
  return reverseMap;
}

function hasChinese(str) {
  return /[\u4e00-\u9fa5]/.test(str)
}

</script>

<template>
  <div class="mt-[100px]">
    <InputText class="w-1/2" type="text" v-model="input"
               placeholder="paste full team (format: pokemon1 / pokemon2 ... / pokemon6)"
               @keyup.enter="queryTeam"/>
    <TreeSelect filter :options="teamTierNodes" :placeholder="teamTier" @node-select="onNodeSelect"/>
    <div class="mt-2">
      <Button label="Submit" @click="queryTeam"/>
      <TeamInfo :teamId="teamInfoId" :teamTier="teamTier" :searchPrivate="true" v-if="teamInfoId"></TeamInfo>
    </div>
  </div>

</template>