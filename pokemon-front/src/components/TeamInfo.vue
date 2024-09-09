<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref, watch} from "vue";
import Team from "@/components/Team.vue";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import Accordion from 'primevue/accordion';
import AccordionTab from 'primevue/accordiontab';

import ProgressSpinner from "primevue/progressspinner";

const props = defineProps({
  teamId: {
    type: String,
    required: true
  }
});

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const teamInfo = ref()
const loading = ref(true);
const loadFail = ref(false);

async function queryTeam(teamId) {
  loading.value = true;
  teamInfo.value = null
  const res = await fetch(`${apiUrl}/api/team/${teamId}`, {
        method: "GET"
      }
  )
  try {
    if (res.ok) {
      teamInfo.value = await res.json();
      loading.value = false;

      // 让每支队伍里面的宝可梦按同种顺序排列，避免混乱
      sortPokemons(teamInfo.value.pokemons);
      sortPokemons(teamInfo.value.teamSet.pokemons);

      if (teamInfo.value.similarTeams?.length > 0) {
        for (let similarTeam of teamInfo.value.similarTeams) {
          sortPokemons(similarTeam.pokemons);
        }
      }

    } else {
      loading.value = false;
      loadFail.value = true;
    }
  } catch (e) {
    console.log("query team fail")
    loading.value = false;
    loadFail.value = true;
  }
}

function sortPokemons(pokemons) {
  pokemons.sort((a, b) => {
    return a.name.localeCompare(b.name)
  });
}

queryTeam(props.teamId);

watch(() => props.teamId, async (newTeamId) => {
  await queryTeam(newTeamId);
});
</script>

<template>
  <div v-if="teamInfo" v-show="loading===false && loadFail===false">
    <!--team-->
    <p class="font-bold">team</p>
    <Team :team="teamInfo" :compact="true" :teamSet="teamInfo?.teamSet"></Team>
    <Accordion>
      <AccordionTab header="set" :headerStyle='{"font-weight": 700}'>
        <div class="flex gap-10">
          <div v-for="pokemon in teamInfo?.teamSet.pokemons" class="min-w-36">
            <p>
              {{ pokemon.name }}
            </p>
            <div v-if="pokemon.items">
              <p class="text-red-500">{{ "@" + (pokemon.items?.length === 0 ? "???" : pokemon.items[0]) }}</p>
            </div>

            <div class="flex items-center gap-1">
              <p>{{ "Tera Type: " + pokemon.teraTypes }}</p>
            </div>
            <div v-if="pokemon.moves && pokemon.moves.length !==0">
              <p v-for="move in pokemon.moves.slice(0, 4)">
                <span class="text-blue-500">{{ "-" + move }}</span>
              </p>
              <br/>
            </div>
            <p v-if="pokemon.moves.length > 4 || pokemon.items.length > 1" class="font-light">alternative sets:</p>
            <p v-for="item in pokemon.items.slice(1, pokemon.items.length)">
              <span class="font-light">{{ "@" + item }}</span>
            </p>
            <p v-for="move in pokemon.moves.slice(4, pokemon.moves.length)">
              <span class="font-light">{{ "-" + move }}</span>
            </p>
          </div>
        </div>
      </AccordionTab>
    </Accordion>

    <!--similar teams-->
    <p class="font-bold mt-3">similar teams</p>
    <span v-if="!teamInfo.similarTeams || teamInfo.similarTeams.length ===0">NA</span>
    <div v-else class="mt-2" v-for="similarTeam in teamInfo.similarTeams">
      <div class="flex items-center gap-1">
        <Team :team="similarTeam" :compact="true" :teamSet="similarTeam?.teamSet"></Team>
        <i class="ml-2 pi pi-eye cursor-pointer" style="font-size: 1rem"
           @click="queryTeam(similarTeam.id.data)"/>
      </div>
    </div>

    <!--recent replays-->
    <p class="font-bold mt-3">recent replays</p>
    <DataTable :value="teamInfo.teams" sortField="battleDate" :sortOrder="-1" paginator :rows="10">
      <Column field="teamId" header="team" :style="{ width:'20%'}">
        <template #body="slotProps">
          <div class="overflow-visible flex items-center gap-1">
            <Team :team="slotProps.data" :compact="true"></Team>
          </div>
        </template>
      </Column>
      <Column field="playerName" header="playerName" :style="{ width:'10%'}">
        <template #body="{data}">
          <router-link :to="`/player-record?name=${data.playerName}`" class="text-black">
            {{ data.playerName }}
          </router-link>
        </template>
      </Column>
      <Column field="battleDate" sortable header="date" :style="{ width:'10%'}"/>
      <Column field="rating" sortable header="rating" :style="{ width:'10%'}"/>
      <Column field="battle-example" header="replay" :style="{ width:'20%'}">
        <template #body="{data}">
          <a :href="`https://replay.pokemonshowdown.com/${data.battleId}`" target="_blank" class="text-black">
            {{ data.battleId }}
          </a>
        </template>
      </Column>
    </DataTable>
  </div>
  <ProgressSpinner v-if="loading"/>
  <p v-if="loadFail" class="mt-[60px]">load team fail.</p>
</template>