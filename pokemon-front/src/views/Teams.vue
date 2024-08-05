<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref} from "vue";
import Column from "primevue/column";
import Team from "@/components/Team.vue";
import DataTable from "primevue/datatable";
import ProgressSpinner from 'primevue/progressspinner';
import {useRoute} from "vue-router";

const route = useRoute();
const apiUrl = import.meta.env.VITE_BACKEND_URL;

const teams = ref()
const page = ref(0);
const row = ref(7);

async function queryTeams(page, row) {
  let url = new URL(`${apiUrl}/api/v2/teams?page=${page}&row=${row}`);
  if (route.query.pokemons) {
    url.searchParams.set('pokemons', route.query.pokemons);
  }

  if (route.query.tags) {
    url.searchParams.set('tags', getTeamTagFiled(route.query.tags));
  }

  if (route.query.sort) {
    url.searchParams.set('sort', getSortFiled(route.query.sort));
  }

  const res = await fetch(url,
      {
        method: "GET"
      }
  );
  const response = await res.json();
  teams.value = response;
}

function getSortFiled(sortMode) {

  switch (sortMode) {
    case "rating":
      return "maxRating";
    case "popularity":
      return "uniquePlayerNum";
    case "date":
      return "latestBattleDate";
  }
}

function getTeamTagFiled(teamTag) {
  switch (teamTag) {
    case "Offense":
      return "BALANCE_ATTACK";
    case "Balance":
      return "BALANCE";
    case "HO":
      return "ATTACK";
    case "Stall":
      return ["STAFF", "BALANCE_STAFF"];
  }
}

function onPage(event) {
  queryTeams(event.page, event.rows);
}

queryTeams(page.value, row.value);
</script>

<template>
  <DataTable v-if="teams" :value="teams.data" class="ladder" lazy paginator :rows="row"
             :rowsPerPageOptions="[7, 10, 15]" :totalRecords="teams.totalRecords" @page="onPage($event)"
             :scrollable="false" tableStyle="min-width: 50rem">
    <Column field="teamId" header="team" :style="{ width:'20%'}">
      <template #body="slotProps">
        <div class="flex items-center">
          <Team :team="slotProps.data" :compact="true"></Team>
        </div>
      </template>
    </Column>
    <Column field="uniquePlayerNum" header="use(unique)" :style="{ width:'10%'}"/>
    <Column field="maxRating" header="maxRating" :style="{ width:'10%'}"/>

    <Column field="teams" header="" :style="{ width:'60%'}">
      <template #body="{data}">
        <DataTable :value="data.teams" paginator :rows="10">
          <Column field="playerName" header="playerName" :style="{ width:'10%'}">
            <template #body="{data}">
              <router-link :to="`/player-record?name=${data.playerName}`" class="text-black">
                {{ data.playerName }}
              </router-link>
            </template>
          </Column>
          <Column field="rating" sortable header="rating" :style="{ width:'10%'}"/>
          <Column field="battleDate" sortable header="date" :style="{ width:'10%'}"/>
          <Column field="battle-example" header="replay" :style="{ width:'20%'}">
            <template #body="{data}">
              <a :href="`https://replay.pokemonshowdown.com/${data.battleId}`" target="_blank" class="text-black">
                {{ data.battleId }}
              </a>
            </template>
          </Column>
        </DataTable>
      </template>
    </Column>

  </DataTable>
  <ProgressSpinner v-else/>
</template>

<style scoped>
/*排行榜表格样式*/
.ladder {
  width: 90%;
  margin: 60px auto 0; /*表格下移以适应绝对定位的导航栏*/
}

.team-list {
  border: 0;
  margin: 0;
}
</style>