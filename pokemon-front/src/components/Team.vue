<script setup>
import PokemonSetTip from "@/components/PokemonSetTip.vue";

const props = defineProps({
  team: Object,
  teamSet: Object,
  compact: Boolean
})

function getPokemonConfigMap() {
  if (!props.teamSet) {
    return;
  }

  let pokemonConfigMap = {};
  for (let pokemonSet of props.teamSet.pokemons) {
    pokemonConfigMap[pokemonSet.name] = pokemonSet;
  }

  for (let pokemon of props.team.pokemons) {
    if (!pokemonConfigMap[pokemon.name]) {
      continue;
    }
    let pokemonConfig = pokemonConfigMap[pokemon.name];

    if (pokemonConfig.items && pokemonConfig.items.length !== 0) {
      pokemon.item = pokemonConfig.items[0];
    }

    if (pokemonConfig.abilities && pokemonConfig.abilities.length !== 0) {
      pokemon.ability = pokemonConfig.abilities[0];
    }

    if (pokemonConfig.teraTypes && pokemonConfig.teraTypes.length !== 0) {
      pokemon.teraType = pokemonConfig.teraTypes.join("/");
    }

    if (pokemonConfig.detailChange) {
      pokemon.detailChange = pokemonConfig.detailChange;
    }

    if (pokemonConfig.moves) {
      pokemon.moves = pokemonConfig.moves;
    }
  }
  return pokemonConfigMap;
}

</script>

<template>
  <div class="list-none flex justify-start gap-[15px]">
    <PokemonSetTip :pokemon="pokemon" :pokemonConfigMap="getPokemonConfigMap()" v-for="pokemon in team.pokemons"/>
  </div>
</template>