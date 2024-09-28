<script setup>
import {ref, watch} from "vue";

const props = defineProps({
  team: Object,
  teamSet: Object,
  compact: Boolean
})

const pokemonConfigMap = ref();

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
  if (pokemonConfigMap.value) {
    let pokemonConfig = pokemonConfigMap.value[pokemon.name];
    if (pokemonConfig && pokemonConfig.items) {
      return "@" + (pokemonConfig.items.length === 0 ? "???" : pokemonConfig.items.join("/"));
    }
  }
  return "@" + (pokemon.item == null ? "???" : pokemon.item);
}

function updateTeamConfig(teamSet) {
  if (!teamSet) {
    return;
  }

  pokemonConfigMap.value = {};
  for (let pokemonSet of teamSet.pokemons) {
    pokemonConfigMap.value[pokemonSet.name] = pokemonSet;
  }

  for (let pokemon of props.team.pokemons) {
    if (!pokemonConfigMap.value[pokemon.name]) {
      continue;
    }
    let pokemonConfig = pokemonConfigMap.value[pokemon.name];

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
}

updateTeamConfig(props.teamSet);
watch(() => props.teamSet, async (teamSet) => {
  updateTeamConfig(teamSet);
});
</script>

<template>
  <div :class="compact? 'pokemon-list-compact':'pokemon-list'">
    <div class="set-tip" v-for="pokemon in team.pokemons">
      <div>
        <img :src="getIconUrl(pokemon)" :alt="pokemon.name" :title="pokemon.name"/>
        <img class="absolute h-4 w-4 bottom-0 right-0" v-if="pokemon.item" :src="getItemUrl(pokemon.item)"
             :alt="pokemon.item" :title="pokemon.item"/>
      </div>
      <div class="set-tip-text text-left">
        <p>
          {{ pokemon.name }}
        </p>
        <div v-if="pokemon.item" >
          <span class="text-red-500">{{ getPokemonItemText(pokemon) }}</span>
        </div>

        <div v-if="pokemon.teraType" class="flex items-center gap-1">
          <p class="text-red-500">Tera:</p>
          <img height="17" width="40" v-for="tera in pokemon.teraType.split('/')" :src="getTeraIcon(tera)"
               :alt="pokemon.teraType" :title="pokemon.teraType">
        </div>
        <div v-if="pokemon.moves && pokemon.moves.length !==0">
          <p class="text-blue-500">Top 4 moves:</p>
          <p v-for="move in pokemon.moves.slice(0, 4)">
            <span class="text-blue-500">{{ "-" + move }}</span>
          </p>
          <br/>
          <p class="text-gray-300">Other moves:</p>
          <p v-for="move in pokemon.moves.slice(4, pokemon.moves.length)">
            <span class="text-gray-300">{{ "-" + move }}</span>
          </p>
        </div>
      </div>
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

.set-tip .set-tip-text {
  visibility: hidden;
  background-color: black;
  color: #fff;
  width: 300px;
  /* 定位 */
  position: absolute;
  z-index: 1;
}

.set-tip:hover .set-tip-text {
  visibility: visible;
}
</style>