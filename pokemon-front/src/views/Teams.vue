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
import Dialog from 'primevue/dialog';
import {useRoute, useRouter} from "vue-router";
import TeamInfo from "@/components/TeamInfo.vue";
import LoadingIcon from "@/views/LoadingIcon.vue";

const route = useRoute();
const apiUrl = import.meta.env.VITE_BACKEND_URL;

const loading = ref(true);
const loadFail = ref(false);
const tour = ref(route.query.tour !== undefined);
const teams = ref();
const router = useRouter();
const page = ref(route.query.page ? Number.parseInt(route.query.page) : 0);
const row = ref(route.query.row ? Number.parseInt(route.query.row) : 7);
const teamInfoDialogVisible = ref(false);
const teamInfoId = ref();
const teamTier = ref();
const first = ref(page.value * row.value);

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
    url.searchParams.set('groupName', route.query.range);
  }

  if (route.query.pokepaste) {
    url.searchParams.set('pokepaste', route.query.pokepaste);
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
  window.scrollTo({
    top: 0
  });
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
    case "unexpectedness":
      return "creativityScore";
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
  await router.push({path: route.path, query: {...route.query, page: event.page, row: event.rows}});
}

function toggleTeamInfoDialog(teamId, tier) {
  teamInfoId.value = teamId;
  teamTier.value = tier;
  teamInfoDialogVisible.value = true;
}

queryTeams(page.value, row.value);
</script>

<template>
  <DataTable v-if="teams" v-show="loading===false && loadFail===false" :value="teams.data" class="ladder"
             lazy paginator :first="first" :rows="row" :rowsPerPageOptions="[7, 10, 15]"
             :totalRecords="teams.totalRecords"
             @page="onPage($event)" :scrollable="false" tableStyle="min-width: 50rem">
    <Column field="teamId" :header="$t('team')" :style="{ width:'20%'}">
      <template #body="slotProps">
        <div class="flex items-center gap-1">
          <Team :team="slotProps.data" :compact="true" :teamSet="slotProps.data.teamSet"></Team>
          <i class="ml-2 pi pi-eye cursor-pointer" style="font-size: 1rem"
             @click="toggleTeamInfoDialog(slotProps.data.id.data, slotProps.data.tier)"/>
          <a class="ml-2" target="_blank" v-if="slotProps.data.pokepasts?.length > 0" v-for="pokepast in slotProps.data.pokepasts"
             :href="pokepast.url" >
            <i class="pi pi-link" style="color: darkblue"></i>
          </a>
        </div>
      </template>
    </Column>
    <Column field="uniquePlayerNum" :header="$t('use unique')" :style="{ width:'10%'}"/>

    <Column v-if="!tour" field="maxRating" :header="$t('max rating')" :style="{ width:'10%'}"/>

    <Column field="teams" header="" :style="{ width:'60%'}">
      <template #body="{data}">
        <DataTable :value="data.teams" :sortField="getSort()" :sortOrder="-1" paginator :rows="7">
          <Column v-if="tour" field="player.name" :header="$t('player name')" :style="{ width:'15%'}" headerClass="text-gray-500 text-sm">
            <template #body="{data}">
              <a :href="`/player-record?name=${data.player?.name}&tourPlayer=true`" target="_blank"
                 class="dynamicThemeText underline">
                {{ data.player?.name }}
              </a>
            </template>
          </Column>
          <Column v-else field="playerName" :header="$t('player name')" :style="{ width:'10%'}" headerClass="text-gray-500 text-sm">
            <template #body="{data}">
              <a :href="`/player-record?name=${data.playerName}`" target="_blank"
                 class="dynamicThemeText text-blue-400">
                {{ data.playerName }}
              </a>
            </template>
          </Column>
          <Column v-if="tour" field="playerRecord.winDif" sortable :header="$t('record')" :style="{ width:'10%'}" headerClass="text-gray-500 text-sm">
            <template #body="{data}">
              <span>{{ data.playerRecord?.win + "-" + data.playerRecord?.loss }}</span>
            </template>
          </Column>
          <Column v-if="tour" field="player.team" :header="$t('team')" :style="{ width:'10%'}" headerClass="text-gray-500 text-sm"/>
          <Column v-if="tour" field="stage" :header="$t('stage')" :style="{ width:'10%'}" headerClass="text-gray-500 text-sm"/>
          <Column v-else field="rating" sortable :header="$t('rating')" :style="{ width:'10%'}" headerClass="text-gray-500 text-sm"/>
          <Column field="battleDate" sortable :header="$t('date')" :style="{ width:'10%'}" headerClass="text-gray-500 text-sm"/>
          <Column field="battle-example" :header="$t('replay')" :style="{ width:'20%'}" headerClass="text-gray-500 text-sm">
            <template #body="{data}">
              <a :href="`https://replay.pokemonshowdown.com/${data.battleId}`" target="_blank"
                 class="dynamicThemeText">
                {{ data.battleId }}
              </a>
            </template>
          </Column>
        </DataTable>
      </template>
    </Column>
  </DataTable>
  <Dialog v-model:visible="teamInfoDialogVisible" modal :header="$t('Team Info')" class="size-3/4">
    <div class="">
      <TeamInfo :teamId="teamInfoId" :teamTier="teamTier"></TeamInfo>
    </div>
  </Dialog>
  <LoadingIcon v-if="loading"/>
  <p v-if="loadFail" class="mt-[60px]">load team fail.</p>
</template>

<style scoped>
/*排行榜表格样式*/
.ladder {
  min-width: max-content;
  width: 90%;
  margin: 60px auto 0; /*表格下移以适应绝对定位的导航栏*/
}

.team-list {
  border: 0;
  margin: 0;
}
</style>