<!--
  -  MIT License
  -
  -  Copyright (c) 2025-2025 mimosa
  -->
<script setup>
import {ref} from "vue";
import OverlayPanel from 'primevue/overlaypanel';

const props = defineProps({
  pokemon: Object,
  pokemonConfigMap: {
    type: Object,
    required: false
  }
})

const op = ref();
const toggle = (event) => {
  op.value.toggle(event);
};

function getIconUrl(pokemon) {
  const iconName = pokemon.name.replace(" ", "").replace("-*", "");
  return "/pokemonicon/" + encodeURIComponent(iconName) + ".png";
}

function getItemUrl(item) {
  return "/itemicon/" + item + ".png";
}

function getTeraIcon(tera) {
  return "/types/" + tera + ".png";
}

function getPokemonItemText(pokemon) {
  if (props.pokemonConfigMap) {
    let pokemonConfig = props.pokemonConfigMap[pokemon.name];
    if (pokemonConfig && pokemonConfig.items) {
      return " " + (pokemonConfig.items.length === 0 ? "???" : pokemonConfig.items.join("/"));
    }
  }
  return " " + (pokemon.item == null ? "???" : pokemon.item);
}
</script>

<template>
  <div>
    <div>
      <img :src="getIconUrl(pokemon)" :alt="pokemon.name" @mouseover="toggle" @mouseleave="toggle"/>
      <img class="absolute h-4 w-4 bottom-0 right-0" v-if="pokemon.item" :src="getItemUrl(pokemon.item)"
           :alt="pokemon.item"/>
    </div>
    <OverlayPanel ref="op" pt:content:class="bg-[#F0F0F0E6]">
      <div class="min-w-48 text-left text-black">
        <p class="font-bold">
          {{ pokemon.name }}
        </p>
        <div v-if="pokemon.item">
          <span>Item:</span>
          <span class="font-sans">{{ getPokemonItemText(pokemon) }}</span>
        </div>

        <div v-if="pokemon.teraType" class="flex items-center gap-1">
          <p>Tera:</p>
          <img height="17" width="40" v-for="tera in pokemon.teraType.split('/')" :src="getTeraIcon(tera)"
               :alt="pokemon.teraType" :title="pokemon.teraType">
        </div>
        <div v-if="pokemon.moves && pokemon.moves.length !==0">
          <hr>
          <p v-for="move in pokemon.moves.slice(0, 4)">
            <span class="font-sans">{{ "-" + move }}</span>
          </p>
          <div class="mt-3" v-if="pokemon.moves.slice(4, pokemon.moves.length).length !==0">
            <hr>
            <p class="text-sm">Other moves:</p>
            <p v-for="move in pokemon.moves.slice(4, pokemon.moves.length)">
              <span class="text-sm font-sans">{{ "-" + move }}</span>
            </p>
          </div>
        </div>
      </div>
    </OverlayPanel>
  </div>
</template>
<style scoped>
</style>