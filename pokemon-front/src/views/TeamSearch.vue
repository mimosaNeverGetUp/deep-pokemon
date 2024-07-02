<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import Button from 'primevue/button';
import MultiSelect from 'primevue/multiselect';
import {pokemoninfo} from "@/components/data/pokemoninfo.js"
import {ref} from "vue";

const pokemons = ref()
const selectTags = ref()
const tags = ref(["STAFF","BALANCE","BALANCE_ATTACK","BALANCE_STAFF","ATTACK","UNPOPULAR"])

function getTeamSearchUrl(pokemons, tags) {
  if (!pokemons) {
    pokemons = '';
  }

  if (!tags) {
    tags = '';
  }

  return `/teams?pokemons=${pokemons}&tags=${tags}`;
}
</script>

<template>
  <div class="flex flex-col gap-2 mt-[60px]  ">
    <label >包含精灵</label>
    <MultiSelect v-model="pokemons" :options=" Object.values(pokemoninfo).map(item => item.name)" display="chip" filter
                 placeholder="select pokemons" variant="filled" class="size-auto font-normal min-w-80 min-h-9"  :virtualScrollerOptions="{ itemSize: 44 }"/>
  </div>

  <div class="flex flex-col gap-2 mt-5">
    <label>队伍标签</label>
    <MultiSelect v-model="selectTags" :options="tags" display="chip" placeholder="select team tags" variant="filled"
                 class="w-full md:w-20rem" />
  </div>
  <router-link :to="getTeamSearchUrl(pokemons, selectTags)">
    <Button class="mt-3" icon="pi pi-search" label="Submit"/>
  </router-link>
</template>