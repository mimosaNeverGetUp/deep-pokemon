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

const apiUrl = import.meta.env.VITE_BACKEND_URL;

const input = ref();
const load = ref()
const teamInfoId = ref();
const teamTier = ref("gen9ou");

async function queryTeam() {
  if (input.value) {
    teamInfoId.value = null;
    teamInfoId.value = getTeamId(input.value.split("/"));
  }
}

function getTeamId(team) {
  let numbers = [];
  for (let pokemon of team) {
    let name = pokemon;
    if (pokemon.includes("-*")) {
      name = pokemon.substring(0, pokemon.lastIndexOf('-*'));
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

</script>

<template>
  <div class="mt-[100px] min-w-max">
    <InputText class="w-1/2" type="text" v-model="input"
               placeholder="paste full team (format: pokemon1 / pokemon2 ... / pokemon6)"
               @keyup.enter="queryTeam"/>
    <Button label="Submit" @click="queryTeam"/>
    <TeamInfo :teamId="teamInfoId" :teamTier="teamTier" v-if="teamInfoId"></TeamInfo>
  </div>
</template>