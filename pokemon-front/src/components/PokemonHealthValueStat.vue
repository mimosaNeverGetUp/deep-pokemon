<!--
  -  MIT License
  -
  -  Copyright (c) 2025-2025 mimosa
  -->
<script setup>
import OverlayPanel from 'primevue/overlaypanel';
import {ref} from "vue";

const props = defineProps({
  data: Object
})

const op = ref();
const toggle = (event) => {
  op.value.toggle(event);
};

function getIconUrl(pokemonName) {
  const iconName = pokemonName.replace(" ", "").replace("-*", "")
  return "pokemonicon/" + iconName + ".png"
}

</script>
<template>
  <div class="set-tip text-black">
    <span @mouseover="toggle" @mouseleave="toggle">{{ data.healthValue }}</span>
    <OverlayPanel ref="op" pt:content:class="bg-[#F0F0F0E6]" v-if="data.healthValueStats?.length >0">
      <div class="flex text-black">
        <span class="ml-12 w-16 min-w-16">{{ "dif" }}</span>
        <span class="w-16 min-w-16">{{ "dealt" }}</span>
        <span class="w-16 min-w-16">{{ "taken" }}</span>
      </div>
      <div class="text-left text-black">
        <div v-for="healthValueStat in data.healthValueStats" class="flex">
          <img :src="getIconUrl(healthValueStat.opponentPokemon)" :alt="pokemon" :title="pokemon"/>
          <span class="ml-2 w-16 min-w-16"> {{ healthValueStat.healthValue + "%"}}</span>
          <span class="w-16 min-w-16"> {{(healthValueStat.opponentLossHealthValue) + "%"}}</span>
          <span class="w-16 min-w-16"> {{(healthValueStat.lossHealthValue) + "%"}}</span>
        </div>
      </div>
    </OverlayPanel>
  </div>
</template>