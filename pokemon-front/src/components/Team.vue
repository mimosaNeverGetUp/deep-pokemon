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

    if (pokemonConfig.moves) {
      pokemon.moves = pokemonConfig.moves;
    }
  }
  return pokemonConfigMap;
}

</script>

<template>
  <div :class="compact? 'pokemon-list-compact':'pokemon-list'">
    <div class="set-tip" v-for="pokemon in team.pokemons">
      <PokemonSetTip :pokemon="pokemon" :pokemonConfigMap="getPokemonConfigMap()"/>
    </div>
  </div>
</template>

<style scoped>
.pokemon-list {
  display: flex;
  list-style-type: none;
  justify-content: flex-start;
  gap: 30px;
}

.pokemon-list-compact {
  display: flex;
  list-style-type: none;
  justify-content: flex-start;
  gap: 15px;
}

.set-tip {
  position: relative;
  display: inline-block;
}

</style>