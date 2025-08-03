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
import {ref, inject} from "vue";
import {usePrimeVue} from 'primevue/config';
import {useRoute, useRouter} from "vue-router";
import {useI18n} from 'vue-i18n'

const {locale} = useI18n()

const route = useRoute();
const PrimeVue = usePrimeVue();
const router = useRouter();
const selectPokemon = ref();
const rankComponentRef = ref();
const statHeaderRef = ref();
const showPokemonStat = ref(false);
const formatNodes = [];
const isDarkMode = ref(localStorage.getItem("theme") === "dark");
const headerHeight = inject("headerHeight");

function getFormatNode() {
  for (const formatKey in formats) {
    formatNodes.push({
      key: formatKey.split(".")[0],
      label: formatKey.split(".")[0],
      children: []
    })
  }
}

function updateSelectPokemon(pokemon, showStat) {
  selectPokemon.value = pokemon;
  showPokemonStat.value = showStat;
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

function getStickyHeaderStyle() {
  return {
    top: `${headerHeight}px`
  }
}

function getStickyRankNaviStyle() {
  const top = statHeaderRef.value ? statHeaderRef.value.offsetHeight + headerHeight : 80;
  return {
    top: `${top}px`
  }
}

function clickMenu() {
  showPokemonStat.value = false;
  redirectPositionFunction();
}

function getScrollHeaderMargin() {
  const margin = statHeaderRef.value ? statHeaderRef.value.offsetHeight + headerHeight : 80;
  return {
    "scroll-margin-top": `${margin}px`
  }
}

getFormatNode();
</script>
<template>
  <div class="relative">
    <div class="w-full z-1 sticky dark:bg-(--surface-0) pb-4 pt-4" :style="getStickyHeaderStyle()" ref="statHeaderRef">
      <div class="flex gap-1 items-center justify-end">
        <Avatar v-if="isDarkMode" icon="pi pi-sun" class="dynamicThemeBg dynamicThemeText cursor-pointer" size="large"
                @click="changeSunTheme()"/>
        <Avatar v-else icon="pi pi-moon" class="dynamicThemeBg dynamicThemeText cursor-pointer"
                size="large" @click="changeDarkTheme()"/>
        <Avatar icon="pi pi-language" class="dynamicThemeBg dynamicThemeText cursor-pointer" size="large"
                @click="changeLocales()"/>
        <TreeSelect filter :options="formatNodes" :placeholder="route.query.format" @node-select="onNodeSelect"/>
      </div>
      <div class="flex items-center mt-2 mb-2 xl:hidden">
        <i class="pi pi-bars cursor-pointer hover:bg-green-200" @click="clickMenu()"></i>
        <p class="ml-4 cursor-pointer hover:bg-green-200" @click="clickMenu()">{{ route.query.format }}</p>
        <i v-if="showPokemonStat" class="pi pi-angle-right ml-4"></i>
        <p v-if="showPokemonStat" class="ml-4">{{ selectPokemon?.name ? $t(selectPokemon?.name) : '' }}</p>
      </div>
    </div>
    <div :class="showPokemonStat? 'max-xl:hidden':''">
      <MetaStat :format="route.query.format"/>
    </div>

    <div ref="rankComponentRef" class="relative flex gap-20 mt-16 max-xl:mt-4" :style="getScrollHeaderMargin()">
      <StatsRank :class="showPokemonStat? 'max-xl:hidden sticky h-fit max-xl:w-full':'sticky h-fit max-xl:w-full'"
                 :style="getStickyRankNaviStyle()" :updateSelectPokemon="updateSelectPokemon"
                 :format="route.query.format" :language="route.query.language"/>
      <div :class="showPokemonStat? 'max-lg:overflow-hidden':'max-xl:hidden'">
        <PokemonStat :pokemon="selectPokemon" :format="route.query.format" :language="route.query.language"
                     :redirectPositionFunction="redirectPositionFunction"/>
      </div>
    </div>
  </div>
</template>