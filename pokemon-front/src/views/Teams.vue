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
import DataView from 'primevue/dataview';
import Divider from "primevue/divider";
import Button from 'primevue/button';

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
  <DataTable v-if="teams" v-show="loading===false && loadFail===false" :value="teams.data"
             class="w-5/6 mt-[60px] mx-auto max-lg:hidden"
             lazy paginator :first="first" :rows="row" :rowsPerPageOptions="[7, 10, 15]"
             :totalRecords="teams.totalRecords" @page="onPage($event)" :scrollable="false">
    <Column field="teamId" :header="$t('team')" headerClass="whitespace-nowrap">
      <template #body="slotProps">
        <div class="flex items-center gap-1">
          <Team :team="slotProps.data" :compact="true" :teamSet="slotProps.data.teamSet"></Team>
          <i class="ml-2 pi pi-eye cursor-pointer" style="font-size: 1rem"
             @click="toggleTeamInfoDialog(slotProps.data.id.data, slotProps.data.tier)"/>
          <a class="ml-2" target="_blank" v-if="slotProps.data.pokepasts?.length > 0"
             v-for="pokepast in slotProps.data.pokepasts" :href="pokepast.url">
            <i class="pi pi-link text-blue-400" ></i>
          </a>
        </div>
      </template>
    </Column>
    <Column v-if="!tour" field="maxRating" :header="$t('max rating')" headerClass="whitespace-nowrap"/>
    <Column field="uniquePlayerNum" :header="$t('use unique')" headerClass="whitespace-nowrap"/>
    <Column field="teams" header="">
      <template #body="{data}">
        <DataTable :value="data.teams" :sortField="getSort()" :sortOrder="-1" paginator :rows="7">
          <Column v-if="tour" field="player.name" :header="$t('player name')" :style="{ width:'15%'}"
                  headerClass="text-gray-500 text-sm whitespace-nowrap">
            <template #body="{data}">
              <a :href="`/player-record?name=${data.player?.name}&tourPlayer=true`" target="_blank"
                 class="text-blue-400">
                {{ data.player?.name }}
              </a>
            </template>
          </Column>
          <Column v-else field="playerName" :header="$t('player name')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm whitespace-nowrap">
            <template #body="{data}">
              <a :href="`/player-record?name=${data.playerName}`" target="_blank"
                 class="text-blue-400">
                {{ data.playerName }}
              </a>
            </template>
          </Column>
          <Column v-if="tour" field="playerRecord.winDif" sortable :header="$t('record')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm whitespace-nowrap">
            <template #body="{data}">
              <span>{{ data.playerRecord?.win + "-" + data.playerRecord?.loss }}</span>
            </template>
          </Column>
          <Column v-if="tour" field="player.team" :header="$t('team')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm whitespace-nowrap" bodyClass="text-gray-500 text-sm"/>
          <Column v-if="tour" field="stage" :header="$t('stage')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm whitespace-nowrap" bodyClass="text-gray-500 text-sm"/>
          <Column v-else field="rating" sortable :header="$t('rating')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm whitespace-nowrap"/>
          <Column field="battleDate" sortable :header="$t('date')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm whitespace-nowrap" bodyClass="text-gray-500 text-sm"/>
          <Column field="battle-example" :header="$t('replay')" :style="{ width:'20%'}"
                  headerClass="text-gray-500 text-sm whitespace-nowrap">
            <template #body="{data}">
              <a :href="`https://replay.pokemonshowdown.com/${data.battleId}`" target="_blank"
                 class="text-gray-500 text-sm">
                {{ data.battleId }}
              </a>
            </template>
          </Column>
        </DataTable>
      </template>
    </Column>
  </DataTable>
  <DataView v-if="teams" v-show="loading===false && loadFail===false" :value="teams.data"
            class="lg:hidden mt-[3rem]" layout="list"
            lazy paginator :first="first" :rows="row" paginatorTemplate="FirstPageLink PageLinks LastPageLink"
            :totalRecords="teams.totalRecords" @page="onPage($event)" :scrollable="false">
    <template #list="slotProps">
      <div v-for="(item, index) in slotProps.items" :key="index" class="">
        <div class="mb-1">
          <Team :team="item" :compact="true" :teamSet="item.teamSet"></Team>

          <p v-if="!tour" class="ml-2 mt-2 font-sans text-gray-500">
            {{ $t('max rating description', {rating: item.maxRating})}}
          </p>
          <p class="ml-2 mt-2 font-sans text-gray-500"> {{ $t('unique use description', {uniquePlayerNum: item.uniquePlayerNum}) }} </p>
          <p class="ml-2 mt-2 font-sans text-gray-500" v-if="item.pokepasts?.length > 0">
            {{$t('pokepaste url')}}
            <a class="ml-2" target="_blank" v-for="pokepast in item.pokepasts" :href="pokepast.url">
              <i class="pi pi-link text-blue-400" ></i>
            </a>
          </p>
          <Button class="border-blue-400 font-sans text-sm text-blue-400 ml-2 mt-2  px-2 py-1"
                  outlined :label="$t('team detail')" @click="toggleTeamInfoDialog(item.id.data, item.tier)" />
        </div>

        <DataTable :value="item.teams" :sortField="getSort()" :sortOrder="-1"
                   paginator :rows="4" paginatorTemplate="PageLinks NextPageLink" class="">
          <Column v-if="tour" field="player.name" :header="$t('player name')" :style="{ width:'15%'}"
                  headerClass="text-gray-500 text-sm">
            <template #body="{data}">
              <a :href="`/player-record?name=${data.player?.name}&tourPlayer=true`" target="_blank"
                 class="block max-sm:w-24 break-all text-blue-400 text-sm">
                {{ data.player?.name }}
              </a>
            </template>
          </Column>
          <Column v-else field="playerName" :header="$t('player name')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm">
            <template #body="{data}">
              <a :href="`/player-record?name=${data.playerName}`" target="_blank"
                 class="block max-sm:w-24 break-all text-blue-400 text-sm">
                {{ data.playerName }}
              </a>
            </template>
          </Column>
          <Column v-if="tour" field="playerRecord.winDif" sortable :header="$t('record')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm">
            <template #body="{data}">
              <span class="text-gray-500 text-sm">{{ data.playerRecord?.win + "-" + data.playerRecord?.loss }}</span>
            </template>
          </Column>
          <Column v-if="tour" field="stage" :header="$t('stage')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm">
            <template #body="{data}">
              <a :href="`https://replay.pokemonshowdown.com/${data.battleId}`" target="_blank"
                 class="text-blue-400 text-sm">
                {{ data.stage }}
              </a>
            </template>
          </Column>
          <Column v-else field="rating" sortable :header="$t('rating')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm" bodyClass="text-gray-500 text-sm"/>
          <Column v-if="!tour" field="battleDate" sortable :header="$t('date')" :style="{ width:'10%'}"
                  headerClass="text-gray-500 text-sm" >
            <template #body="{data}">
              <a :href="`https://replay.pokemonshowdown.com/${data.battleId}`" target="_blank"
                 class="text-blue-400 text-sm">
                {{ data.battleDate }}
              </a>
            </template>
          </Column>
        </DataTable>
        <Divider type="solid" class="mb-8"/>
      </div>
    </template>
  </DataView>
  <Dialog v-model:visible="teamInfoDialogVisible" modal :header="$t('Team Info')" class="">
    <div class="">
      <TeamInfo :teamId="teamInfoId" :teamTier="teamTier"></TeamInfo>
    </div>
  </Dialog>
  <LoadingIcon v-if="loading"/>
  <p v-if="loadFail" class="mt-[60px]">load team fail.</p>
</template>

<style scoped>
</style>