<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import StatsRank from '@/components/stats/StatsRank.vue'
import PokemonStat from '@/components/stats/PokemonStat.vue';
import MetaStat from '@/components/stats/MetaStat.vue';
import TreeSelect from 'primevue/treeselect';

import {formats} from "@/components/data/format.js";
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";

const route = useRoute();
const router = useRouter();
const selectPokemon = ref();
const formatNodes = [];

function getFormatNode() {
  for (const formatKey in formats) {
    formatNodes.push({
      key: formatKey.split(".")[0],
      label: formatKey.split(".")[0],
      children: []
    })
  }
}

function updateSelectPokemon(pokemon) {
  selectPokemon.value = pokemon;
}

async function onNodeSelect(event) {
  await router.push({path: '/stats', query: {format: event.key}});
  router.go(0);
}

getFormatNode();
</script>
<template>
  <TreeSelect filter :options="formatNodes" :placeholder="route.query.format" class="mt-[30px]"
              @node-select="onNodeSelect"/>
  <MetaStat :format="route.query.format" class="mb-4"/>
  <div class="flex gap-2">
    <StatsRank :updateSelectPokemon="updateSelectPokemon" :format="route.query.format"/>
    <PokemonStat :pokemon="selectPokemon" :format="route.query.format"/>
  </div>
</template>