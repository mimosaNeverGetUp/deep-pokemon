<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import {ref} from "vue";
const pokemons = ref()
const tags = ref()

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
    <InputText class="size-auto text-sm font-normal min-w-80 min-h-9" type="text" v-model="pokemons" variant="filled"
               placeholder="pokemon which in team(Full English Name)"/>
  </div>

  <div class="flex flex-col gap-2 mt-5">
    <label>队伍标签</label>
    <InputText class="size-auto text-sm font-normal min-w-80 min-h-9" type="text" v-model="tags" variant="filled"
               placeholder="select team tag"/>
  </div>
  <router-link :to="getTeamSearchUrl(pokemons, tags)">
    <Button class="mt-3" icon="pi pi-search" label="Submit"/>
  </router-link>
</template>

<style scoped>

</style>