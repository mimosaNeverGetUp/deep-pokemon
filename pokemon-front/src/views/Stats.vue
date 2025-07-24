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
import {usePrimeVue} from 'primevue/config';
import {useRoute, useRouter} from "vue-router";
import { useI18n } from 'vue-i18n'
const { locale } = useI18n()

const route = useRoute();
const PrimeVue = usePrimeVue();
const router = useRouter();
const selectPokemon = ref();
const rankComponentRef = ref();
const formatNodes = [];
const isDarkMode = ref(localStorage.getItem("theme") === "dark");

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

function changeLocales() {
  locale.value = locale.value === "zh" ? "en" : "zh";
  localStorage.setItem('locale', locale.value);
}

function redirectPositionFunction() {
  if (window.scrollY && window.scrollY > 0) {
    rankComponentRef.value.scrollIntoView();
  }
}

async function changeDarkTheme() {
  document.documentElement.setAttribute("page-theme", "dark");
  PrimeVue.changeTheme('aura-light-green', 'aura-dark-green', 'theme-link', () => {
  });
  localStorage.setItem('theme', 'dark');
  isDarkMode.value = true;
}

async function changeSunTheme() {
  document.documentElement.setAttribute("page-theme", "light  ");
  PrimeVue.changeTheme('aura-dark-green', 'aura-light-green', 'theme-link', () => {
  });
  localStorage.setItem('theme', 'light');
  isDarkMode.value = false;
}

getFormatNode();
</script>
<template>
  <div>
    <div class="mt-[30px]">
      <div class="gap-1 flex items-center justify-end">
        <Avatar v-if="isDarkMode" icon="pi pi-sun" class="dynamicThemeBg dynamicThemeText cursor-pointer" size="large"
                @click="changeSunTheme()"/>
        <Avatar v-else icon="pi pi-moon" class="dynamicThemeBg dynamicThemeText cursor-pointer"
                size="large" @click="changeDarkTheme()"/>
        <Avatar icon="pi pi-language" class="dynamicThemeBg dynamicThemeText cursor-pointer" size="large" @click="changeLocales()"/>
        <TreeSelect filter :options="formatNodes" :placeholder="route.query.format" @node-select="onNodeSelect"/>
      </div>
      <MetaStat :format="route.query.format" class="mb-4"/>
    </div>
    <div ref="rankComponentRef" class="relative flex gap-2 scroll-mt-20">
      <StatsRank class="sticky top-20 h-fit" :updateSelectPokemon="updateSelectPokemon"
                 :format="route.query.format" :language="route.query.language"/>
      <PokemonStat :pokemon="selectPokemon" :format="route.query.format" :language="route.query.language"
                   :redirectPositionFunction="redirectPositionFunction"/>
    </div>
  </div>
</template>