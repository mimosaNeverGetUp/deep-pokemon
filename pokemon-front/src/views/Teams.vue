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
import Dialog from 'primevue/dialog';
import {useRoute} from "vue-router";
import TeamInfo from "@/components/TeamInfo.vue";

const route = useRoute();
const apiUrl = import.meta.env.VITE_BACKEND_URL;

const loading = ref(true);
const loadFail = ref(false);
const tour = ref(route.query.tour !== undefined);
const teams = ref();
const page = ref(0);
const row = ref(7);
const teamInfoDialogVisible = ref(false);
const teamInfoId = ref();

async function queryTeams(page, row) {
  let url = new URL(`${apiUrl}/api/v2/teams?page=${page}&row=${row}`);
  if (route.query.pokemons) {
    url.searchParams.set('pokemons', route.query.pokemons);
  }

  if (route.query.players) {
    url.searchParams.set('players', route.query.players);
  }

  if (route.query.tags) {
    url.searchParams.set('tags', getTeamTagFiled(route.query.tags));
  }

  if (route.query.stages) {
    url.searchParams.set('stages', route.query.stages);
  }

  if (route.query.sort) {
    url.searchParams.set('sort', getSortFiled(route.query.sort));
  }

  if (route.query.range) {
    url.searchParams.set('groupName', getTeamGroupName(route.query.range));
  }

  const res = await fetch(url,
      {
        method: "GET"
      }
  );

  if (res.ok) {
    const response = await res.json();
    teams.value = response;
    loading.value = false;
  } else {
    loadFail.value = true;
    loading.value = false;
  }
}

function getSortFiled(sortMode) {
  switch (sortMode) {
    case "rating":
      return "maxRating";
    case "popularity":
      return "uniquePlayerNum";
    case "date":
      return "latestBattleDate";
    case "win rate":
      return "maxPlayerWinRate";
    case "win dif":
      return "maxPlayerWinDif";
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

function getTeamGroupName(range) {
  switch (range) {
    case "Last 3 days":
      return "last_3_days";
    case "Last week":
      return "last_7_days";
    case "Last month":
      return "last_30_days";
    case "Last 3 months":
      return "last_90_days";
  }
  return range;
}

function getSort() {
  if (route.query.sort === "date") {
    return "battleDate";
  }

  if (tour.value) {
    return "playerRecord.winDif";
  } else {
    return "rating";
  }
}

async function onPage(event) {
  loading.value = true;
  await queryTeams(event.page, event.rows);
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
}

function toggleTeamInfoDialog(teamId) {
  teamInfoDialogVisible.value = true;
  teamInfoId.value = teamId;
}

queryTeams(page.value, row.value);
</script>

<template>
  <DataTable v-if="teams" v-show="loading===false && loadFail===false" :value="teams.data" class="ladder"
             lazy paginator :rows="row" :rowsPerPageOptions="[7, 10, 15]" :totalRecords="teams.totalRecords"
             @page="onPage($event)" :scrollable="false" tableStyle="min-width: 50rem">
    <Column field="teamId" header="team" :style="{ width:'20%'}">
      <template #body="slotProps">
        <div class="flex items-center gap-1">
          <Team :team="slotProps.data" :compact="true" :teamSet="slotProps.data.teamSet"></Team>
          <i class="ml-2 pi pi-eye cursor-pointer" style="font-size: 1rem"
             @click="toggleTeamInfoDialog(slotProps.data.id.data)"/>
        </div>
      </template>
    </Column>
    <Column field="uniquePlayerNum" header="use(unique)" :style="{ width:'5%'}"/>

    <Column v-if="!tour" field="maxRating" header="maxRating" :style="{ width:'10%'}"/>

    <Column field="teams" header="" :style="{ width:'60%'}">
      <template #body="{data}">
        <DataTable :value="data.teams" :sortField="getSort()" :sortOrder="-1" paginator :rows="10">
          <Column v-if="tour" field="player.name" header="playerName" :style="{ width:'15%'}">
            <template #body="{data}">
              <router-link :to="`/player-record?name=${data.player.name}&tourPlayer=true`" class="text-black">
                {{ data.player.name }}
              </router-link>
            </template>
          </Column>
          <Column v-else field="playerName" header="playerName" :style="{ width:'10%'}">
            <template #body="{data}">
              <router-link :to="`/player-record?name=${data.playerName}`" class="text-black">
                {{ data.playerName }}
              </router-link>
            </template>
          </Column>
          <Column v-if="tour" field="playerRecord.winDif" sortable header="record" :style="{ width:'10%'}">
            <template #body="{data}">
              <span>{{ data.playerRecord?.win + "-" + data.playerRecord?.loss }}</span>
            </template>
          </Column>
          <Column v-if="tour" field="player.team" header="team" :style="{ width:'10%'}"/>
          <Column v-if="tour" field="stage" header="stage" :style="{ width:'10%'}"/>
          <Column v-else field="rating" sortable header="rating" :style="{ width:'10%'}"/>
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

  <Dialog v-model:visible="teamInfoDialogVisible" modal header="Team Info" class="size-3/4">
    <div class="">
      <TeamInfo :teamId="teamInfoId"></TeamInfo>
    </div>
  </Dialog>
  <ProgressSpinner v-if="loading"/>
  <p v-if="loadFail" class="mt-[60px]">load team fail.</p>
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