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
import {useRoute} from "vue-router";

const route = useRoute();
const apiUrl = import.meta.env.VITE_BACKEND_URL;

const teams = ref()
const page = ref(0);
const row = ref(15);


async function queryTeams(page, row) {

  let url = new URL(`${apiUrl}/api/teams?page=${page}&row=${row}`);
  if (route.query.pokemons) {
    url.searchParams.set('pokemons', route.query.pokemons);
  }

  if (route.query.tags) {
    url.searchParams.set('tags', route.query.tags);
  }

  const res = await fetch(url,
      {
        method: "GET"
      }
  );
  const response = await res.json();
  teams.value = response;
  totalRecords.value = response.totalRecords;
}

function onPage(event) {
  queryTeams(event.page, event.rows);
}

queryTeams(page.value, row.value);
</script>

<template>
  <DataTable :value="teams.data" class="ladder" lazy paginator :rows="20" :rowsPerPageOptions="[5, 10, 20, 50]"
             :totalRecords="teams.totalRecords" @page="onPage($event)" :scrollable="false" stripedRows
             tableStyle="min-width: 50rem">
    <Column field="team" header="队伍" :style="{ width:'30%' }">
      <template #body="{data}">
        <div class="team-list">
          <Team :team="data.team"></Team>
        </div>
      </template>
    </Column>
    <Column field="playerName" header="玩家名" :style="{ width:'20%' }">
      <template #body="{data}">
        <router-link :to="`/player-record?name=${data.team.playerName}`">
          {{ data.team.playerName }}
        </router-link>
      </template>
    </Column>
    <Column field="battle-example" header="replay" :style="{ width:'20%' }">
      <template #body="{data}">
        <a :href="`https://replay.pokemonshowdown.com/${data.battleID}`">
          {{ data.battleId }}
        </a>
      </template>
    </Column>

  </DataTable>
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