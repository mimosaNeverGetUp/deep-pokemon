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

const selectPokemon = ref();

function updateSelectPokemon(pokemon) {
  selectPokemon.value = pokemon;
}

</script>

<template>
  <MetaStat class="mt-[30px] mb-4"/>
  <div class="flex gap-2">
    <StatsRank :updateSelectPokemon="updateSelectPokemon"/>
    <PokemonStat :pokemon="selectPokemon"/>
  </div>
</template>