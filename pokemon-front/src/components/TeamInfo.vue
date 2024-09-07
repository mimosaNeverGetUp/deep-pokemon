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

async function queryTeam() {
  const res = await fetch(`${apiUrl}/api/team/${props.teamId}`, {
        method: "GET"
      }
  )
  try {
    if (res.ok) {
      teamInfo.value = await res.json();
      loading.value = false;
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

queryTeam();

watch(() => props.teamId, async (newTeamId) => {
  loading.value = true;
  teamInfo.value = null
  await queryTeam();
});
</script>

<template>
  <div v-if="teamInfo" v-show="loading===false && loadFail===false">
    <p class="font-bold">team</p>
    <Team :team="teamInfo" :compact="true" :teamSet="teamInfo?.teamSet"></Team>
    <p class="font-bold mt-3">recent replays</p>
    <DataTable :value="teamInfo.teams" sortField="battleDate" :sortOrder="-1" paginator :rows="10">
      <Column field="teamId" header="team" :style="{ width:'20%'}">
        <template #body="slotProps">
          <div class="h-20 overflow-visible flex items-center gap-1">
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