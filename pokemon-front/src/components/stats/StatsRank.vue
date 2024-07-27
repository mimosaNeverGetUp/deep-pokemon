<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref} from "vue";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const usages = ref(null)
const currentPage = ref(0);
const page = ref(0);
const row = ref(20);
const totalRecords = ref(null);

const props = defineProps({
  updateSelectPokemon: Function,
  format: String
})

async function fetchStatsData(format, page, row) {
  usages.value = null
  const res = await fetch(`${apiUrl}/api/stats/${format}/usage?&page=${page}&row=${row}`, {
        method: "GET"
      }
  )
  const response = await res.json()
  usages.value = response.data;
  totalRecords.value = response.totalRecords;
  props.updateSelectPokemon(usages.value[0]);
}

function onPage(event) {
  currentPage.value = event.page
  fetchStatsData(props.format, event.page, event.rows)
}

function convertToPercentage(f) {
  return (f * 100).toFixed(2) + '%'
}

function getIconUrl(pokemon) {
  const iconName = pokemon.replace(" ", "").replace("-*", "")
  return "/pokemonicon/" + iconName + ".png"
}

function onRowSelect(row) {
  props.updateSelectPokemon(row.data);
}

fetchStatsData(props.format, page.value, row.value);
</script>

<template>
  <DataTable :value="usages" class="w-2/5" lazy paginator :rows="row" :rowsPerPageOptions="[20, 30, 40 ,50, 100]"
             :totalRecords="totalRecords" @page="onPage($event)" :scrollable="false" selectionMode="single" dataKey="id"
             @rowSelect="onRowSelect">
    <Column field="rank" header="rank" :style="{ width:'5%' }">
      <template #body="{index}">{{ currentPage * row + index + 1 }}</template>
    </Column>
    <Column field="name" header="pokemon" :style="{ width:'5%' }">
      <template #body="{data}">
        <div class="flex justify-start items-center">
          <img :src="getIconUrl(data.name)" :alt="data.name"
               :title="data.name"/>
          <span> {{ data.name }}</span>
        </div>
      </template>
    </Column>
    <Column field="count" header="count" :style="{ width:'5%' }"/>
    <Column field="usage.weighted" header="weighted" :style="{ width:'5%' }">
      <template #body="{data}">{{ convertToPercentage(data.usage.weighted) }}</template>
    </Column>
    <Column field="usage.raw" header="raw" :style="{ width:'5%' }">
      <template #body="{data}">{{ convertToPercentage(data.usage.raw) }}</template>
    </Column>
    <Column field="usage.real" header="real" :style="{ width:'5%' }">
      <template #body="{data}">{{ convertToPercentage(data.usage.real) }}</template>
    </Column>
  </DataTable>
</template>