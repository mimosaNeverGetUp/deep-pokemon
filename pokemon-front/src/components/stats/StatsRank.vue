<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref} from "vue";
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import RankDif from "@/components/stats/RankDif.vue";
import {zh_translation_text} from "@/components/data/translationText.js";

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const usages = ref(null)
const currentPage = ref(0);
const page = ref(0);
const row = ref(20);
const totalRecords = ref(null);

const props = defineProps({
  updateSelectPokemon: Function,
  format: String,
  language: {
    type: String,
    required: false,
    default: "en"
  }
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
  currentPage.value = event.page;
  row.value = event.rows;
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

function getTranslation(text) {
  if (props.language === "zh" && zh_translation_text[text]) {
    return zh_translation_text[text];
  }

  return text;
}

fetchStatsData(props.format, page.value, row.value);
</script>

<template>
  <DataTable :value="usages" class="" lazy paginator :rows="row" :rowsPerPageOptions="[20, 30, 40 ,50, 100]"
             :totalRecords="totalRecords" @page="onPage($event)" :scrollable="false" selectionMode="single" dataKey="id"
             @rowSelect="onRowSelect">
    <Column field="rank" header="rank" :style="{ width:'5%' }">
      <template #body="{data}">{{ data.rank }}</template>
    </Column>
    <Column field="name" header="pokemon" :style="{ width:'35%' }">
      <template #body="{data}">
        <div class="flex gap-1 items-center justify-start">
          <img :src="getIconUrl(data.name)" :alt="data.name" :title="data.name"/>
          <span class=" w-full min-w-24"> {{ getTranslation(data.name) }}</span>
          <RankDif :newValue="data.rank" :oldValue="data.lastMonthUsage?.rank"/>
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
  </DataTable>
</template>