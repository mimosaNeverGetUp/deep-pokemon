<script setup>
defineProps({
  team: Object
})

function getIconUrl(pokemon) {
  const iconName = pokemon.name.replace(" ","").replace("-*","")
  return "/pokemonicon/" + iconName + ".png"
}

function getPokemonItemText(pokemon) {
  return pokemon.name + "@" + (pokemon.item == null ? "???" : pokemon.item)
}
</script>

<template>
  <div class="pokemon-list">
    <div class="set-tip" v-for="pokemon in team.pokemons">
      <img :src="getIconUrl(pokemon)" :alt="pokemon.name"
           :title="pokemon.name"/>
      <div class="set-tip-text">
        <p>
          {{ getPokemonItemText(pokemon) }}
        </p>
        <p v-for="move in pokemon.moves">
          <span>{{ "-" + move }}</span>
        </p>
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

.set-tip {
  position: relative;
  display: inline-block;
}

.set-tip .set-tip-text {
  visibility: hidden;
  background-color: black;
  color: #fff;
  width: 180px;
  /* 定位 */
  position: absolute;
  z-index: 1;
  top: -5px;
  right: 105%;

}

.set-tip:hover .set-tip-text {
  visibility: visible;
}
</style>