<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import Button from 'primevue/button';
import MultiSelect from 'primevue/multiselect';
import SelectButton from 'primevue/selectbutton';

import {pokemoninfo} from "@/components/data/pokemoninfo.js"
import {ref} from "vue";

const pokemons = ref()
const selectTags = ref()
const selectedSort = ref("rating")
const selectedRange = ref("Last week")
const tags = ref(["Offense", "Balance", "HO", "Stall"]);
const ranges = ref(["Last 3 days", "Last week", "Last month", "Last 3 months"]);
const sortModes = ref(["rating", "popularity", "date"])

function getTeamSearchUrl(pokemons, tags, range, sort) {
  if (!pokemons) {
    pokemons = '';
  }

  if (!tags) {
    tags = '';
  }

  return `/teams?pokemons=${pokemons}&tags=${tags}&sort=${sort}&range=${range}`;
}
</script>

<template>
  <div class="flex flex-col gap-2 mt-[60px]">
    <span>包含精灵</span>
    <MultiSelect v-model="pokemons" :options=" Object.values(pokemoninfo).map(item => item.name)" display="chip" filter
                 placeholder="select pokemons" variant="filled" class="size-auto font-normal min-w-80 min-h-9"
                 :virtualScrollerOptions="{ itemSize: 44 }"/>
  </div>

  <div class="flex flex-col gap-2 mt-5">
    <span>队伍标签</span>
    <SelectButton v-model="selectTags" :options="tags" aria-labelledby="basic"/>
    <span>Sort mode</span>
    <SelectButton v-model="selectedSort" :options="sortModes" aria-labelledby="basic"/>
    <span>Range</span>
    <SelectButton v-model="selectedRange" :options="ranges" aria-labelledby="basic"/>


  </div>
  <router-link :to="getTeamSearchUrl(pokemons, selectTags, selectedRange, selectedSort)">
    <Button class="mt-3" icon="pi pi-search" label="Submit"/>
  </router-link>
</template>