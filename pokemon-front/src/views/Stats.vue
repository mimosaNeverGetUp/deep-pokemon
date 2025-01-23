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
import Avatar from 'primevue/avatar';

import {formats} from "@/components/data/format.js";
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";

const route = useRoute();
const router = useRouter();
const selectPokemon = ref();
const rankComponentRef = ref();
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
  await router.push({path: '/stats', query: {format: event.key, language: route.query.language}});
  router.go(0);
}

function getStatsLink() {
  let language = route.query.language === "zh" ? "en" : "zh";
  let format = route.query.format;
  return `/stats?format=${format}&language=${language}`
}

function redirectPositionFunction() {
  if (window.scrollY && window.scrollY > 0) {
    rankComponentRef.value.scrollIntoView();
  }
}


getFormatNode();
</script>
<template>
  <div>
    <div class="mt-[30px]">
      <div class="gap-1 flex items-center justify-end">
        <router-link :to="getStatsLink()">
          <Avatar icon="pi pi-language" class="bg-white dark:bg-[#181818] dark:text-[#EBEBEBA3]" size="large"/>
        </router-link>
        <TreeSelect filter :options="formatNodes" :placeholder="route.query.format" @node-select="onNodeSelect"/>
      </div>
      <MetaStat :format="route.query.format" class="mb-4"/>
    </div>
    <div ref="rankComponentRef" class="relative flex gap-2 scroll-mt-20">
      <StatsRank  class="sticky top-20 h-fit" :updateSelectPokemon="updateSelectPokemon"
                 :format="route.query.format" :language="route.query.language"/>
      <PokemonStat :pokemon="selectPokemon" :format="route.query.format" :language="route.query.language"
          :redirectPositionFunction="redirectPositionFunction"/>
    </div>
  </div>
</template>