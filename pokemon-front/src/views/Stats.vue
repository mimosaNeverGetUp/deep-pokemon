<!--
  -  MIT License
  -
  -  Copyright (c) 2024-2024 mimosa
  -->

<script setup>
import StatsRank from '@/components/stats/StatsRank.vue'
import PokemonStat from '@/components/stats/PokemonStat.vue';
import MetaStat from '@/components/stats/MetaStat.vue';
import {ref} from "vue";
import {useRoute} from "vue-router";

const route = useRoute();
const selectPokemon = ref();
const format = ref('gen9ou');

function updateSelectPokemon(pokemon) {
  selectPokemon.value = pokemon;
}
</script>

<template>
  <MetaStat :format="route.query.format" class="mt-[30px] mb-4"/>
  <div class="flex gap-2">
    <StatsRank :updateSelectPokemon="updateSelectPokemon" :format="route.query.format "/>
    <PokemonStat :pokemon="selectPokemon" :format="route.query.format "/>
  </div>
</template>