<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import {ref} from "vue";
import {FilterMatchMode} from 'primevue/api';
import InputText from 'primevue/inputtext';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import MultiSelect from 'primevue/multiselect';
import RankDif from "@/components/stats/RankDif.vue";
import {Dex} from '@pkmn/dex';

const apiUrl = import.meta.env.VITE_BACKEND_URL;
const usages = ref(null)
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

// get current tier
const genRegex = /gen([0-9]+)/g;
let currentTier;
let currentTierNumber;
if (props.format.includes("1v1")) {
  currentTier = props.format.substring(0, props.format.indexOf("1v1"));
  currentTierNumber = currentTier.matchAll(genRegex).next().value[1];
} else if (props.format.includes("2v2")) {
  currentTier = props.format.substring(0, props.format.indexOf("2v2"));
  currentTierNumber = currentTier.matchAll(genRegex).next().value[1];
} else {
  currentTier = props.format.matchAll(genRegex).next().value[0];
  currentTierNumber = props.format.matchAll(genRegex).next().value[1];
}


const types = []
for (const type of Dex.forGen(currentTierNumber).types.all()) {
  types.push(type.name)
}

const filters = ref({
  name: { value: null, matchMode: FilterMatchMode.CONTAINS },
  types: { value: null, matchMode: FilterMatchMode.CONTAINS },
});

async function fetchStatsData(format) {
  usages.value = null
  const res = await fetch(`${apiUrl}/api/stats/${format}/usage`, {
        method: "GET"
      }
  )
  const response = await res.json();
  for (let usage of response.data) {
    usage.types = Dex.forGen(currentTierNumber).species.get(usage.name)?.types;
  }
  usages.value = response.data;
  totalRecords.value = response.totalRecords;
  props.updateSelectPokemon(usages.value[0]);
}

function onPage(event) {
  props.updateSelectPokemon(usages.value[event.first]);
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

fetchStatsData(props.format);
</script>

<template>
  <DataTable v-model:filters="filters" :value="usages" :totalRecords="totalRecords" @page="onPage($event)" scrollable
             scrollHeight="720px" selectionMode="single" dataKey="id" @rowSelect="onRowSelect" filterDisplay="row"
             pt:wrapper:class="no-scrollbar">
    <Column field="rank" header=" " :style="{ width:'10%' }">
      <template #body="{data}">{{ data.rank }}</template>
    </Column>
    <Column field="name" :header="$t('pokemon')" :style="{ width:'35%' }" headerClass="text-gray-500 text-sm"
            :showFilterMenu="false" >
      <template #body="{data}">
        <div class="flex gap-1 items-center justify-start">
          <img :src="getIconUrl(data.name)" :alt="data.name" :title="data.name"/>
          <span class=" w-full min-w-24 font-bold"> {{ $t(data.name) }}</span>
          <RankDif :newValue="data.rank" :oldValue="data.lastMonthUsage?.rank"/>
        </div>
      </template>
      <template #filter="{ filterModel, filterCallback }">
        <InputText class="min-w-24 max-w-24 text-gray-500 text-sm" v-model="filterModel.value" type="text" @input="filterCallback()"
                   :placeholder="$t('filter')" />
      </template>
    </Column>
    <Column field="usage.weighted" :header="$t('weighted')" :style="{ width:'5%' }" headerClass="text-gray-500 text-sm">
      <template  #body="{data}">
        <span class="text-gray-500">
          {{ convertToPercentage(data.usage.weighted) }}
        </span>
      </template>
    </Column>
    <Column field="types" :header="$t('types')" :style="{ width:'5%' }" :showFilterMenu="false" headerClass="text-gray-500 text-sm">
      <template #body="{data}">
        <div class="flex gap-1">
          <img v-for="type in data.types"
               :src="`/types/${type}.png`" height="15" width="36" :alt="type"/>
        </div>
      </template>
      <template #filter="{ filterModel, filterCallback }">
        <MultiSelect class="size-10" v-model="filterModel.value" @change="filterCallback()" :options="types"
                     placeholder="filter">
        </MultiSelect>
      </template>
    </Column>
  </DataTable>
</template>
<style>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}

/* Hide scrollbar for IE, Edge and Firefox */
.no-scrollbar {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}
</style>