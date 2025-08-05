<script setup>
import {ref} from "vue";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Team from '@/components/Team.vue'
import LoadingIcon from "@/views/LoadingIcon.vue";
import {avatar} from "@/components/data/avatar.js";


// 在需要使用后端 URL 的地方
const apiUrl = import.meta.env.VITE_BACKEND_URL;
const loading = ref(true);
const loadFail = ref(false);
const rank = ref(null)
const page = ref(0);
const row = ref(20);
const totalRecords = ref(null);
const emptyTeam = ref({
  pokemons: [{name: "null"}, {name: "null"}, {name: "null"}, {name: "null"}, {name: "null"}, {name: "null"}]
});

async function fetchData(page, row) {
  rank.value = null
  const res = await fetch(`${apiUrl}/api/rank?page=${page}&row=${row}`, {
        method: "GET"
      }
  )

  if (res.ok) {
    const response = await res.json();
    rank.value = response.data;
    totalRecords.value = response.totalRecords;
    loading.value = false;
  } else {
    loadFail.value = true;
    loading.value = false;
  }

}

async function onPage(event) {
  loading.value = true;
  await fetchData(event.page, event.rows);
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
}

function getDefaultPlayerImage() {
  return "unknownf.png";
}

function getPlayerIcon(playerIcon) {
  if (!playerIcon) {
    return getDefaultPlayerImage();
  }

  if (playerIcon.icon in avatar) {
    return `https://play.pokemonshowdown.com/sprites/trainers/${avatar[playerIcon.icon]}.png`;
  }

  if (playerIcon.icon.charAt(0) === '#') {
    return `https://play.pokemonshowdown.com/sprites/trainers-custom/${playerIcon.icon.substr(1)}.png`;
  }

  return `https://play.pokemonshowdown.com/sprites/trainers/${playerIcon.icon}.png`;
}

fetchData(page.value, row.value)
</script>

<template>
  <DataTable :value="rank" v-show="loading===false && loadFail===false" class="ladder lg:w-3/4" lazy paginator
             :rows="20" :totalRecords="totalRecords" @page="onPage($event)" paginatorTemplate="PageLinks NextPageLink">
    <Column field="rank" :header="$t('rank')" class="text-gray-500 text-sm max-sm:p-1"
            headerClass="text-gray-500 text-sm"></Column>
    <Column field="name" :header="$t('player name')" headerClass="text-gray-500 text-sm">
      <template #body="{data}">
        <router-link :to="`/player-record?name=${data.name}`" class="" target="_blank">
          <div class="flex items-center gap-1">
            <img :src="getPlayerIcon(data.playerIcon)"
                 class="ladder-player-avatar bg-transparent" alt=""/>
            <p class="max-sm:max-w-36 text-blue-400 break-all">
              {{ data.name }}
            </p>
          </div>
        </router-link>
      </template>
    </Column>
    <Column field="elo" :header="$t('elo')" headerClass="text-gray-500 text-sm" class="max-sm:p-1"></Column>
    <Column field="gxe" :header="$t('gxe')" headerClass="text-gray-500 text-sm" class="max-sm:p-1"></Column>
    <Column field="recentTeam" class="text-center max-md:hidden"
            headerClass="text-gray-500 text-sm">
      <template #header>
        <div class="flex-1 text-center">{{$t('recent teams')}}</div>
      </template>
      <template #body="{data}">
        <div class="flex justify-center overflow-visible">
          <div class="team-list" v-if="data.recentTeam.length !== 0">
            <Team v-for="team in data.recentTeam" :team="team" :compact="true"></Team>
          </div>
          <div v-else>
            <Team :team="emptyTeam" :compact="true"></Team>
          </div>
        </div>
      </template>
    </Column>
  </DataTable>
  <LoadingIcon v-if="loading"/>
  <p v-if="loadFail" class="mt-[60px]">load ladder fail.</p>
</template>

<style scoped>
/*排行榜表格样式*/
.ladder {
  min-width: max-content;
  margin: 60px auto 0; /*表格下移以适应绝对定位的导航栏*/
}

.team-list {
  border: 0;
  margin: 0;
}

.ladder-player-avatar {
  width: 55px;
  height: 55px;
  border: 1px solid black;
  border-radius: 50%;
  -webkit-border-radius: 50%;
  -moz-border-radius: 50%;
}
</style>